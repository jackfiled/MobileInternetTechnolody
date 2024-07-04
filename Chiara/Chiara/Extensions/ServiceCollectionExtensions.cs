using Chiara.Abstractions;
using Chiara.Services;

namespace Chiara.Extensions;

public static class ServiceCollectionExtensions
{
    public static void AddChiaraService(this IServiceCollection serviceCollection)
    {
        serviceCollection.AddSingleton<LocalFileService>();
        serviceCollection.AddSingleton<FileService>();
        serviceCollection.AddSingleton<IFileStore, FileService>(provider => provider.GetRequiredService<FileService>());
        serviceCollection.AddTransient<IMediaRepositoryScanner, DefaultMediaRepositoryScanner>();
        serviceCollection.AddTransient<RefreshService>();
        serviceCollection.AddHostedService<MigrationService>();
        serviceCollection.AddHostedService<FileService>(provider => provider.GetRequiredService<FileService>());
        serviceCollection.AddSingleton<FfmpegService>();
    }
}
