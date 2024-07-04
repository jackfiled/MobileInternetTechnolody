using Chiara.Abstractions;

namespace Chiara.Models;

public class Song : IMediaItem
{
    public int Id { get; set; }

    public string Title { get; init; } = string.Empty;

    public string Arist { get; init; } = string.Empty;

    public string Path { get; init; } = string.Empty;

    public string CoverImageUrl { get; init; } = string.Empty;

    public string Url { get; init; } = string.Empty;

    public int AlbumId { get; set; }

    public required Album Album { get; set; }
}
