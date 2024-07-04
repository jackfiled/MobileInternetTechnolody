using Chiara.Models;
using Microsoft.EntityFrameworkCore;

namespace Chiara.Services;

public class ChiaraDbContext(DbContextOptions<ChiaraDbContext> options) : DbContext(options)
{
    public DbSet<Song> Songs { get; init; }

    public DbSet<Album> Albums { get; init; }

    public DbSet<DatabaseFile> Files { get; init; }

    public DbSet<ShowSeason> Seasons { get; init; }

    public DbSet<Episode> Episodes { get; init; }

    public DbSet<MediaRepository> Repositories { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Album>()
            .HasMany(e => e.Songs)
            .WithOne(e => e.Album)
            .HasForeignKey(e => e.AlbumId)
            .IsRequired();

        modelBuilder.Entity<ShowSeason>()
            .HasMany(s => s.Episodes)
            .WithOne(e => e.Season)
            .HasForeignKey(e => e.ShowSeasonId)
            .IsRequired();

        modelBuilder.Entity<MediaRepository>()
            .HasMany(m => m.Albums)
            .WithOne(a => a.ParentRepository)
            .HasForeignKey(a => a.ParentRepositoryId)
            .IsRequired();

        modelBuilder.Entity<MediaRepository>()
            .HasMany(m => m.Seasons)
            .WithOne(s => s.ParentRepository)
            .HasForeignKey(s => s.ParentRepositoryId)
            .IsRequired();

        modelBuilder.Entity<DatabaseFile>()
            .HasIndex(f => f.FileId);
    }
}
