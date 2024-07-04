using Chiara.Abstractions;

namespace Chiara.Models;

public class Album : IMediaItem, IEquatable<Album>
{
    public int Id { get; set; }

    public string Title { get; init; } = string.Empty;

    public string Arist { get; init; } = string.Empty;

    public string Path { get; init; } = string.Empty;

    public string CoverImageUrl { get; set; } = string.Empty;

    public List<Song> Songs { get; set; } = [];

    public int ParentRepositoryId { get; set; }

    public required MediaRepository ParentRepository { get; set; }

    public bool Equals(Album? other)
    {
        if (other is null)
        {
            return false;
        }

        return Title == other.Title && Arist == other.Arist;
    }

    public override bool Equals(object? obj)
    {
        return obj is Album other && Equals(other);
    }

    public override int GetHashCode()
    {
        return Title.GetHashCode() ^ Arist.GetHashCode();
    }
}
