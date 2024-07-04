using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Storage;

namespace Chiara.Services;

public class MigrationService(IServiceProvider serviceProvider) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        using IServiceScope scope = serviceProvider.CreateScope();
        ChiaraDbContext dbContext = scope.ServiceProvider.GetRequiredService<ChiaraDbContext>();

        await EnsureDatabaseAsync(dbContext, stoppingToken);
        await RunMigrationAsync(dbContext, stoppingToken);
    }

    private static async Task EnsureDatabaseAsync(ChiaraDbContext dbContext, CancellationToken cancellationToken)
    {
        IRelationalDatabaseCreator creator = dbContext.GetService<IRelationalDatabaseCreator>();
        IExecutionStrategy strategy = dbContext.Database.CreateExecutionStrategy();

        await strategy.ExecuteAsync(async () =>
        {
            if (!await creator.ExistsAsync(cancellationToken))
            {
                await creator.CreateAsync(cancellationToken);
            }
        });
    }

    private static async Task RunMigrationAsync(ChiaraDbContext dbContext, CancellationToken cancellationToken)
    {
        IExecutionStrategy strategy = dbContext.Database.CreateExecutionStrategy();

        await strategy.ExecuteAsync(async () =>
        {
            await using IDbContextTransaction transaction =
                await dbContext.Database.BeginTransactionAsync(cancellationToken);
            await dbContext.Database.MigrateAsync(cancellationToken);
            await transaction.CommitAsync(cancellationToken);
        });
    }
}
