using System.ComponentModel.DataAnnotations;
using Chiara.Models;

namespace Chiara.DataTransferObjects;

public class ShowSeasonResponse
{
    [Required]
    public int Id { get; set; }

    [Required]
    public string Title { get; set; } = string.Empty;

    [Required]
    public List<EpisodeResponse> Episodes { get; set; } = [];

    public ShowSeasonResponse()
    {

    }

    public ShowSeasonResponse(ShowSeason showSeason)
    {
        Id = showSeason.Id;
        Title = showSeason.Title;
        Episodes.AddRange(showSeason.Episodes.Select(e => new EpisodeResponse(e)));
    }
}
