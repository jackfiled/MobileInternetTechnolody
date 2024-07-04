using System.ComponentModel.DataAnnotations;

namespace Chiara.DataTransferObjects;

public class CreateMediaRepositoryRequest
{
    [Required]
    public string Name { get; set; } = string.Empty;

    [Required]
    public string Path { get; set; } = string.Empty;
}
