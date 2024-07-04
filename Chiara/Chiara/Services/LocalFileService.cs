using System.Collections.Concurrent;
using Chiara.Models;

namespace Chiara.Services;

public class LocalFileService
{
    private readonly ConcurrentDictionary<Guid, LocalFile> _idMap = [];

    private readonly ConcurrentDictionary<string, LocalFile> _pathMap = [];

    public Task<string> ClarifyFile(string path, string contentType)
    {
        LocalFile localFile = new(path, contentType);

        if (!localFile.File.Exists)
        {
            throw new InvalidOperationException();
        }

        if (_pathMap.TryGetValue(localFile.File.FullName, out LocalFile? existedFile))
        {
            return Task.FromResult(existedFile.FileId.ToString());
        }

        _idMap.TryAdd(localFile.FileId, localFile);
        _pathMap.TryAdd(localFile.File.FullName, localFile);

        return Task.FromResult(localFile.FileId.ToString());
    }

    public LocalFile? ReadFile(Guid guid)
    {
        return _idMap.GetValueOrDefault(guid);
    }
}
