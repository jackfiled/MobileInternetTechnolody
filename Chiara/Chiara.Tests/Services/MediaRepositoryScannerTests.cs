using Chiara.Abstractions;
using Chiara.Models;
using Chiara.Services;
using Chiara.Tests.Utils;
using Microsoft.Extensions.Logging;

namespace Chiara.Tests.Services;

public class MediaRepositoryScannerTests
{
    private readonly DefaultMediaRepositoryScanner _scanner = new(
        MockCreator.CreateEmptyMock<IFileStore>(),
        MockCreator.CreateEmptyMock<ILogger<DefaultMediaRepositoryScanner>>());

    [Fact]
    public async Task ScanTest()
    {
        MediaRepository repository = new() { Path = "/home/ricardo/Documents/Code/CSharp/Chiara/Chiara.Tests/Data" };

        List<Album> albums = (await _scanner.ScanAlbumAsync(repository)).ToList();

        Assert.Single(albums);
        Assert.Contains(albums, a => a.Title == "Genshin Impact - Jade Moon Upon a Sea of Clouds");
        Assert.Contains(albums, a => a.Arist == "Yu-Peng Chen, HOYO-MiX");
        Assert.Contains(albums, a => a.Songs.Count == 69);
    }
}
