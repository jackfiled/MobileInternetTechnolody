using Chiara.Abstractions;

namespace Chiara.Models;

public class ShowSeason : IMediaItem, IEquatable<ShowSeason>
{
    public int Id { get; set; }

    public string Title { get; init; } = string.Empty;

    public string Arist { get; init; } = string.Empty;

    public string Path { get; init; } = string.Empty;

    public List<Episode> Episodes { get; set; } = [];

    public int ParentRepositoryId { get; set; }

    public required MediaRepository ParentRepository { get; set; }

    public bool Equals(ShowSeason? other)
    {
        if (other is null)
        {
            return false;
        }

        return Title == other.Title && Arist == other.Arist;
    }

    public override bool Equals(object? obj) => obj is ShowSeason other && Equals(other);

    public override int GetHashCode() => Title.GetHashCode() ^ Title.GetHashCode();
}
