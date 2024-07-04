using System.Security.Cryptography;
using System.Threading.Channels;
using Chiara.Abstractions;
using Chiara.Models;
using Microsoft.EntityFrameworkCore;

namespace Chiara.Services;

public sealed class FileService(
    IServiceProvider serviceProvider,
    LocalFileService localFileService,
    ILogger<FileService> logger)
    : BackgroundService, IFileStore
{
    private readonly Channel<DatabaseFile> _channel = Channel.CreateUnbounded<DatabaseFile>(new UnboundedChannelOptions
    {
        SingleReader = true
    });

    public async Task<string> UploadFileAsync(ReadOnlyMemory<byte> buffer, string contentType)
    {
        using IServiceScope scope = serviceProvider.CreateScope();
        await using ChiaraDbContext dbContent = scope.ServiceProvider.GetRequiredService<ChiaraDbContext>();

        await using Stream stream = new MemoryStream();
        await stream.WriteAsync(buffer);
        stream.Position = 0;

        using HMACSHA256 hmacsha256 = new([0, 1, 2, 3, 4, 5, 6, 7, 8, 9]);
        byte[] hash = await hmacsha256.ComputeHashAsync(stream);

        DatabaseFile? existedFile = await (from item in dbContent.Files.AsNoTracking()
            where item.HashValue == hash && item.ContentType == contentType
            select item).FirstOrDefaultAsync();

        if (existedFile is not null)
        {
            return existedFile.FileId.ToString();
        }

        DatabaseFile file = new()
        {
            FileId = Guid.NewGuid(), Content = buffer.ToArray(), ContentType = contentType, HashValue = hash
        };

        await _channel.Writer.WriteAsync(file);

        return file.FileId.ToString();
    }

    public Task<string> ClarifyLocalFileAsync(string path, string contentType)
        => localFileService.ClarifyFile(path, contentType);

    public async Task<IFile?> GetFileAsync(Guid guid)
    {
        using IServiceScope scope = serviceProvider.CreateScope();
        await using ChiaraDbContext dbContext = scope.ServiceProvider.GetRequiredService<ChiaraDbContext>();

        DatabaseFile? file = await (from item in dbContext.Files.AsNoTracking()
            where item.FileId == guid
            select item).FirstOrDefaultAsync();

        if (file is not null)
        {
            return file;
        }

        return localFileService.ReadFile(guid);
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        using IServiceScope scope = serviceProvider.CreateScope();
        await using ChiaraDbContext dbContext = scope.ServiceProvider.GetRequiredService<ChiaraDbContext>();

        while (await _channel.Reader.WaitToReadAsync(stoppingToken))
        {
            if (!_channel.Reader.TryRead(out DatabaseFile? file))
            {
                continue;
            }

            logger.LogInformation("Receive file from channel: {}.", file.FileId);
            await dbContext.Files.AddAsync(file, stoppingToken);
            await dbContext.SaveChangesAsync(stoppingToken);
        }
    }
}
