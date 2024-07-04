using Xabe.FFmpeg;

namespace Chiara.Services;

public record VideoConversion(Task<IConversionResult> RunningTask,
    DirectoryInfo CacheDirectory,
    string Name,
    CancellationTokenSource CancellationTokenSource);

public class FfmpegService
{
    public VideoConversion? CurrentConversion { get; set; }

    public async Task<IConversionResult> StartConversion(FileInfo video, DirectoryInfo cacheDirectory, string name,
        CancellationToken cancellationToken)
    {
        IMediaInfo inputVideo = await FFmpeg.GetMediaInfo(video.FullName, cancellationToken);

        IConversion conversion = FFmpeg.Conversions.New()
            .AddStream(inputVideo.Streams)
            .AddParameter("-re", ParameterPosition.PreInput)
            .AddParameter("-c:v h264 -f hls")
            .AddParameter("-profile:v high10")
            .AddParameter("-hls_list_size 10 -hls_time 10 -hls_base_url /api/hls/")
            .AddParameter("-hls_flags delete_segments")
            .SetOutput(Path.Combine(cacheDirectory.FullName, $"{name}.m3u8"));

        return await conversion.Start(cancellationToken);
    }
}
