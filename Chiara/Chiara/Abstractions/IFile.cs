namespace Chiara.Abstractions;

public interface IFile
{
    public Guid FileId { get; }

    public string ContentType { get; }

    public Stream OpenRead();
}
