using Chiara.DataTransferObjects;
using Chiara.Models;
using Chiara.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Chiara.Controllers;

[Route("api/[controller]")]
[ApiController]
public class MediaRepositoryController(RefreshService refreshService, ChiaraDbContext dbContext) : ControllerBase
{
    [HttpGet]
    [ProducesResponseType<IEnumerable<MediaRepositoryResponse>>(200)]
    public async Task<IActionResult> ListRepositories()
    {
        return Ok(await (from item in dbContext.Repositories.AsNoTracking()
            select new MediaRepositoryResponse(item)).ToListAsync());
    }

    [HttpGet("{id}")]
    [ProducesResponseType<MediaRepositoryResponse>(200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> GetRepository([FromRoute] int id)
    {
        MediaRepository? repository = await (from item in dbContext.Repositories.AsNoTracking()
                .Include(m => m.Albums)
                .Include(m => m.Seasons)
            where item.Id == id
            select item).FirstOrDefaultAsync();

        if (repository is null)
        {
            return NotFound();
        }

        return Ok(new MediaRepositoryResponse(repository));
    }

    [HttpPost]
    [ProducesResponseType<MediaRepositoryResponse>(201)]
    public async Task<IActionResult> CreateRepository([FromBody] CreateMediaRepositoryRequest request)
    {
        MediaRepository repository = new() { Name = request.Name, Path = request.Path };

        await dbContext.Repositories.AddAsync(repository);
        await dbContext.SaveChangesAsync();

        return Created($"api/MediaRepository/{repository.Id}", new MediaRepositoryResponse(repository));
    }

    [HttpPost("{id}")]
    [ProducesResponseType<MediaRepositoryResponse>(200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> RefreshRepository([FromRoute] int id)
    {
        IQueryable<MediaRepository> mediaRepositoryQuery = from item in dbContext.Repositories
                .Include(m => m.Albums)
                .Include(m => m.Seasons)
            where item.Id == id
            select item;

        MediaRepository? repository = await mediaRepositoryQuery.FirstOrDefaultAsync();

        if (repository is null)
        {
            return NotFound();
        }

        await refreshService.Refresh(repository);

        return Ok(new MediaRepositoryResponse(await mediaRepositoryQuery.AsNoTracking().FirstAsync()));
    }
}
