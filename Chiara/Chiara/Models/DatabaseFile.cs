using System.ComponentModel.DataAnnotations;
using Chiara.Abstractions;

namespace Chiara.Models;

public class DatabaseFile : IFile
{
    public int Id { get; set; }

    public Guid FileId { get; set; }

    public byte[] Content { get; set; } = [];

    public byte[] HashValue { get; set; } = [];

    [MaxLength(20)]
    public string ContentType { get; set; } = string.Empty;

    public Stream OpenRead()
    {
        MemoryStream stream = new(Content);
        return stream;
    }
}
