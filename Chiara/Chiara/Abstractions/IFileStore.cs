using Chiara.Models;

namespace Chiara.Abstractions;

public interface IFileStore
{
    public Task<string> UploadFileAsync(ReadOnlyMemory<byte> buffer, string contentType);

    public Task<string> ClarifyLocalFileAsync(string path, string contentType);

    public Task<IFile?> GetFileAsync(Guid guid);
}
