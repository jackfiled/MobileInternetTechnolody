using Chiara.Models;

namespace Chiara.DataTransferObjects;

public class MediaRepositoryResponse
{
    public int Id { get; set; }

    public string Path { get; set; } = string.Empty;

    public string Name { get; set; } = string.Empty;

    public List<AlbumResponse> AlbumResponses { get; set; } = [];

    public List<ShowSeasonResponse> ShowSeasonResponses { get; set; } = [];

    public MediaRepositoryResponse()
    {

    }

    public MediaRepositoryResponse(MediaRepository repository)
    {
        Id = repository.Id;
        Path = repository.Path;
        Name = repository.Name;
        AlbumResponses.AddRange(repository.Albums.Select(a => new AlbumResponse(a)));
        ShowSeasonResponses.AddRange(repository.Seasons.Select(s => new ShowSeasonResponse(s)));
    }
}
