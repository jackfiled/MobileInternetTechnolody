using System.ComponentModel.DataAnnotations;
using Chiara.Models;

namespace Chiara.DataTransferObjects;

public class AlbumResponse
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
    public List<SongResponse> Songs { get; set; } = [];

    public AlbumResponse()
    {

    }

    public AlbumResponse(Album album)
    {
        Id = album.Id;
        Title = album.Title;
        Arist = album.Arist;
        CoverImageUrl = album.CoverImageUrl;
        Songs = album.Songs.Select(s => new SongResponse(s)).ToList();
    }
}
