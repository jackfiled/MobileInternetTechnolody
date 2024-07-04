using Chiara.Abstractions;

namespace Chiara.Models;

public class LocalFile(string path, string contentType) : IFile
{
    public Guid FileId { get; } = Guid.NewGuid();

    public FileInfo File { get; } = new(path);

    public string ContentType { get; } = contentType;

    public Stream OpenRead()
    {
        return File.OpenRead();
    }
}
