using System.ComponentModel.DataAnnotations;
using Chiara.Models;

namespace Chiara.DataTransferObjects;

public class EpisodeResponse
{
    [Required]
    public int Id { get; set; }

    [Required]
    public string Title { get; set; } = string.Empty;

    [Required]
    public string EpisodeNumber { get; set; } = string.Empty;

    [Required]
    public int ShowSeasonId { get; set; }

    public EpisodeResponse()
    {

    }

    public EpisodeResponse(Episode episode)
    {
        Id = episode.Id;
        Title = episode.Title;
        EpisodeNumber = episode.EpisodeNumber;
        ShowSeasonId = episode.ShowSeasonId;
    }
}
