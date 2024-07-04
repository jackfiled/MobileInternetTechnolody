using System.ComponentModel.DataAnnotations;

namespace Chiara.DataTransferObjects;

public class VideoPlayingResponse
{
    [Required]
    public string PlayList { get; set; } = string.Empty;
}
