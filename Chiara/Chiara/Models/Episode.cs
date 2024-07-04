using Chiara.Abstractions;

namespace Chiara.Models;

public class Episode : IMediaItem, IEquatable<Episode>
{
    public int Id { get; set; }

    public string Title { get; init; } = string.Empty;

    public string Arist { get; init; } = string.Empty;

    public string EpisodeNumber { get; init; } = string.Empty;

    public string Path { get; init; } = string.Empty;

    public int ShowSeasonId { get; set; }

    public required ShowSeason Season { get; set; }

    public bool Equals(Episode? other)
    {
        if (other is null)
        {
            return false;
        }

        return Title == other.Title && Arist == other.Arist && EpisodeNumber == other.EpisodeNumber;
    }

    public override bool Equals(object? obj)
    {
        return obj is Episode other && Equals(other);
    }

    public override int GetHashCode() => Title.GetHashCode() ^ Arist.GetHashCode() ^ EpisodeNumber.GetHashCode();
}
