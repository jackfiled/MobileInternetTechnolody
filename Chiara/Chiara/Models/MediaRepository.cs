namespace Chiara.Models;

public class MediaRepository
{
    public int Id { get; set; }

    public string Name { get; set; } = string.Empty;

    public string Path { get; set; } = string.Empty;

    public List<Album> Albums { get; set; } = [];

    public List<ShowSeason> Seasons { get; set; } = [];
}
