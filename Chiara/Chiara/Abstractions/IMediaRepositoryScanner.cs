using Chiara.Models;

namespace Chiara.Abstractions;

public interface IMediaRepositoryScanner
{
    public Task<IEnumerable<Album>> ScanAlbumAsync(MediaRepository repository,
        CancellationToken cancellationToken = default);

    public Task<IEnumerable<ShowSeason>> ScanShowAsync(MediaRepository repository,
        CancellationToken cancellationToken = default);
}
