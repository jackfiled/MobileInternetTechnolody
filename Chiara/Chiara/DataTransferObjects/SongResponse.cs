using System.ComponentModel.DataAnnotations;
using Chiara.Models;

namespace Chiara.DataTransferObjects;

public class SongResponse
{
    [Required]
    public int Id { get; set; }

    [Required]
    public string Title { get; set; } = string.Empty;

    [Required]
    public string Arist { get; set; } = string.Empty;

    [Required]
    public string CoverImageUrl { get; set; } = string.Empty;

    [Required]
    public string Url { get; set; } = string.Empty;

    public SongResponse()
    {

    }

    public SongResponse(Song song)
    {
        Id = song.Id;
        Title = song.Title;
        Arist = song.Arist;
        CoverImageUrl = song.CoverImageUrl;
        Url = song.Url;
    }
}
