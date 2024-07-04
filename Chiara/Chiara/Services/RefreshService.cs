using Chiara.Abstractions;
using Chiara.Models;

namespace Chiara.Services;

public class RefreshService(
    ChiaraDbContext dbContext,
    IMediaRepositoryScanner scanner)
{
    public async Task Refresh(MediaRepository repository)
    {
        dbContext.Albums.RemoveRange(repository.Albums);
        dbContext.Seasons.RemoveRange(repository.Seasons);
        await dbContext.SaveChangesAsync();

        IEnumerable<Album> albums = await scanner.ScanAlbumAsync(repository);

        foreach (Album album in albums)
        {
            List<Song> songs = album.Songs;
            album.Songs = [];
            repository.Albums.Add(album);
            await dbContext.SaveChangesAsync();

            songs.ForEach(s => s.AlbumId = album.Id);

            await dbContext.Songs.AddRangeAsync(songs);
            await dbContext.SaveChangesAsync();
        }

        IEnumerable<ShowSeason> seasons = await scanner.ScanShowAsync(repository);

        foreach (ShowSeason season in seasons)
        {
            List<Episode> episodes = season.Episodes;
            season.Episodes = [];
            repository.Seasons.Add(season);
            await dbContext.SaveChangesAsync();

            episodes.ForEach(s => s.ShowSeasonId = season.Id);
            await dbContext.Episodes.AddRangeAsync(episodes);
            await dbContext.SaveChangesAsync();
        }
    }
}
