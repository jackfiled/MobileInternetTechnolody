using System.Net.Mime;
using Chiara.DataTransferObjects;
using Chiara.Models;
using Chiara.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;

namespace Chiara.Controllers;

[ApiController]
[Route("api/[controller]")]
public class HlsController(
    FfmpegService ffmpegService,
    ChiaraDbContext dbContext,
    ILogger<HlsController> logger,
    IOptions<ChiaraOptions> chiaraOptions) : ControllerBase
{
    private readonly DirectoryInfo _cacheDirectory = new(chiaraOptions.Value.TemporaryDirectory);

    [HttpGet("{file}")]
    public IActionResult File([FromRoute] string file)
    {
        FileInfo fileInfo = new(Path.Combine(_cacheDirectory.FullName, file));

        if (!fileInfo.Exists)
        {
            return NotFound();
        }

        if (file.EndsWith("m3u8"))
        {
            return File(fileInfo.OpenRead(), "application/x-mpegURL");
        }

        if (file.EndsWith("ts"))
        {
            return File(fileInfo.OpenRead(), "video/MP2T");
        }

        return File(fileInfo.OpenRead(), MediaTypeNames.Text.Plain);
    }

    [HttpPost("start/{episodeId}")]
    [ProducesResponseType<VideoPlayingResponse>(200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> Start([FromRoute] int episodeId)
    {
        Episode? episode = await (from item in dbContext.Episodes.AsNoTracking()
            where item.Id == episodeId
            select item).FirstOrDefaultAsync();

        if (episode is null)
        {
            return NotFound();
        }

        if (ffmpegService.CurrentConversion is not null)
        {
            logger.LogWarning("Stop running transaction rudely.");
            try
            {
                await ffmpegService.CurrentConversion.RunningTask;
                ffmpegService.CurrentConversion = null;
            }
            catch (Exception e)
            {
                logger.LogInformation("Stop transaction encounter: {}.", e);
            }
        }

        logger.LogInformation("Start transaction of {}.", episode.Title);

        FileInfo videoFile = new(episode.Path);
        string name = Guid.NewGuid().ToString();
        CancellationTokenSource source = new();

        VideoConversion conversion = new(
            ffmpegService.StartConversion(videoFile, _cacheDirectory, name, source.Token),
            _cacheDirectory,
            name,
            source);

        ffmpegService.CurrentConversion = conversion;

        using PeriodicTimer time = new(TimeSpan.FromSeconds(1));

        while (!_cacheDirectory.EnumerateFiles().Any(f => f.Name.StartsWith(name)))
        {
            await time.WaitForNextTickAsync();
        }

        return Ok(new VideoPlayingResponse { PlayList = $"/api/hls/{name}.m3u8" });
    }

    [HttpPost("stop")]
    [ProducesResponseType(200)]
    public async Task<IActionResult> Stop()
    {
        if (ffmpegService.CurrentConversion is null || ffmpegService.CurrentConversion.RunningTask.IsCanceled)
        {
            if (ffmpegService.CurrentConversion is not null)
            {
                logger.LogInformation("Stop finished video transaction.");
                try
                {
                    await ffmpegService.CurrentConversion.RunningTask;
                }
                catch (Exception e)
                {
                    logger.LogInformation("Video encounter: {}.", e);
                }

                ffmpegService.CurrentConversion = null;
            }

            return Ok();
        }

        logger.LogInformation("Stop running video transaction.");
        await ffmpegService.CurrentConversion.CancellationTokenSource.CancelAsync();

        try
        {
            await ffmpegService.CurrentConversion.RunningTask;
            ffmpegService.CurrentConversion = null;
        }
        catch (Exception e)
        {
            logger.LogInformation("Video encounter: {}.", e);
        }

        ClearCacheDirectory();

        return Ok();
    }

    private void ClearCacheDirectory()
    {
        IEnumerable<FileInfo> temporaryFiles = from file in _cacheDirectory.EnumerateFiles()
            where file.Extension is ".m3u8" or ".ts"
            select file;

        foreach (FileInfo file in temporaryFiles)
        {
            if (file.Exists)
            {
                file.Delete();
            }
        }
    }
}
