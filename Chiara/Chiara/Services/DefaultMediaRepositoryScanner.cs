using System.Collections.Concurrent;
using AnitomySharp;
using Chiara.Abstractions;
using Chiara.Models;
using TagLib;

namespace Chiara.Services;

public class DefaultMediaRepositoryScanner(
    IFileStore fileStore,
    ILogger<DefaultMediaRepositoryScanner> logger) : IMediaRepositoryScanner
{
    public async Task<IEnumerable<Album>> ScanAlbumAsync(MediaRepository mediaRepository,
        CancellationToken cancellationToken = default)
    {
        DirectoryInfo rootDirectory = new(mediaRepository.Path);

        if (!rootDirectory.Exists)
        {
            throw new InvalidOperationException();
        }

        Dictionary<Album, List<Song>> albums = [];

        foreach (Song song in await ScanSongAsync(rootDirectory, cancellationToken))
        {
            if (albums.TryGetValue(song.Album, out List<Song>? songs))
            {
                songs.Add(song);
            }
            else
            {
                albums.Add(song.Album, [song]);
            }
        }

        return albums.Select(pair =>
        {
            pair.Value.ForEach(s => s.Album = pair.Key);

            pair.Key.Songs = pair.Value;
            pair.Key.CoverImageUrl =
                Random.Shared.GetItems(pair.Key.Songs.Select(s => s.CoverImageUrl).ToArray(), 1)[0];

            return pair.Key;
        });
    }

    public async Task<IEnumerable<ShowSeason>> ScanShowAsync(MediaRepository repository,
        CancellationToken cancellationToken = default)
    {
        DirectoryInfo rootDirectory = new(repository.Path);

        if (!rootDirectory.Exists)
        {
            throw new InvalidOperationException();
        }

        Dictionary<ShowSeason, List<Episode>> shows = [];

        foreach (Episode episode in await ScanVideoAsync(rootDirectory, cancellationToken))
        {
            if (shows.TryGetValue(episode.Season, out List<Episode>? episodes))
            {
                episodes.Add(episode);
            }
            else
            {
                shows.Add(episode.Season, [episode]);
            }
        }

        return shows.Select(pair =>
        {
            pair.Value.ForEach(e =>
            {
                e.Season = pair.Key;
            });

            pair.Key.Episodes = pair.Value;
            return pair.Key;
        });
    }

    private async Task<IEnumerable<Song>> ScanSongAsync(DirectoryInfo directory, CancellationToken cancellationToken)
    {
        ConcurrentBag<Song> songs = [];

        await Parallel.ForEachAsync(directory.EnumerateDirectories(), cancellationToken, async (info, token) =>
        {
            foreach (Song song in await ScanSongAsync(info, token))
            {
                songs.Add(song);
            }
        });

        await Parallel.ForEachAsync(directory.EnumerateFiles(), cancellationToken, async (f, _) =>
        {
            if (!MediaItemTypes.MusicTypes.Contains(f.Extension))
            {
                return;
            }

            try
            {
                TagLib.File tagFile = TagLib.File.Create(f.FullName);

                IPicture? picture = tagFile.Tag.Pictures.FirstOrDefault();
                string? coverImageUrl = null;
                if (picture is not null)
                {
                    coverImageUrl = "/api/file/" + await fileStore.UploadFileAsync(picture.Data.Data, picture.MimeType);
                }

                Song song = new()
                {
                    Title = tagFile.Tag.Title ?? f.Name,
                    Arist = tagFile.Tag.FirstPerformer ?? "Default Arist",
                    Path = f.FullName,
                    Url = $"/api/file/{await fileStore.ClarifyLocalFileAsync(f.FullName, tagFile.MimeType)}",
                    CoverImageUrl = coverImageUrl ?? string.Empty,
                    Album = new Album
                    {
                        // 避免空应用错误
                        Title = tagFile.Tag.Album ?? "Default Album",
                        Arist = tagFile.Tag.FirstAlbumArtist ?? "Default Artist",
                        Path = f.Directory is null ? string.Empty : f.Directory.FullName,
                        ParentRepository = new MediaRepository()
                    }
                };

                songs.Add(song);
            }
            catch (UnsupportedFormatException e)
            {
                logger.LogInformation("Failed to parser file {}: {}.", f.Name, e);
            }
        });

        return songs;
    }

    private async Task<IEnumerable<Episode>> ScanVideoAsync(DirectoryInfo directory,
        CancellationToken cancellationToken)
    {
        ConcurrentBag<Episode> episodes = [];

        await Parallel.ForEachAsync(directory.EnumerateDirectories(), cancellationToken, async (d, token) =>
        {
            foreach (Episode episode in await ScanVideoAsync(d, token))
            {
                episodes.Add(episode);
            }
        });

        await Parallel.ForEachAsync(directory.EnumerateFiles(), cancellationToken, async (f, _) =>
        {
            if (!MediaItemTypes.VideoTypes.Contains(f.Extension))
            {
                return;
            }

            List<Element> elements = Anitomy.Parse(f.Name).ToList();

            Element? titleElement = (from item in elements
                where item.Category == ElementCategory.ElementAnimeTitle
                select item).FirstOrDefault();

            if (titleElement is null)
            {
                return;
            }

            Element? episodeNumberElement = (from item in elements
                where item.Category == ElementCategory.ElementEpisodeNumber
                select item).FirstOrDefault();

            if (episodeNumberElement is null)
            {
                return;
            }

            Element? episodeTitleElement = (from item in elements
                where item.Category == ElementCategory.ElementEpisodeTitle
                select item).FirstOrDefault();

            if (episodeTitleElement is null)
            {
                Episode episode = new()
                {
                    Title = $"{titleElement.Value} E{episodeNumberElement.Value}",
                    Arist = string.Empty,
                    Path = f.FullName,
                    EpisodeNumber = episodeNumberElement.Value,
                    Season = new ShowSeason
                    {
                        Title = titleElement.Value,
                        Arist = string.Empty,
                        Path = f.Directory?.FullName ?? string.Empty,
                        ParentRepository = new MediaRepository()
                    }
                };

                episodes.Add(episode);
            }
            else
            {
                Episode episode = new()
                {
                    Title = episodeTitleElement.Value,
                    Arist = string.Empty,
                    Path = f.FullName,
                    EpisodeNumber = episodeNumberElement.Value,
                    Season = new ShowSeason
                    {
                        Title = titleElement.Value,
                        Arist = string.Empty,
                        Path = f.Directory?.FullName ?? string.Empty,
                        ParentRepository = new MediaRepository()
                    }
                };

                episodes.Add(episode);
            }
        });

        return episodes;
    }
}
