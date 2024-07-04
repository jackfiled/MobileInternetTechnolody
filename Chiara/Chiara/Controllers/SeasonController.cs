using Chiara.DataTransferObjects;
using Chiara.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Chiara.Controllers;

[ApiController]
[Route("api/[controller]")]
public class SeasonController(ChiaraDbContext dbContext) : ControllerBase
{
    [HttpGet]
    [ProducesResponseType<IEnumerable<ShowSeasonResponse>>(200)]
    public async Task<IActionResult> ListSeasons()
    {
        return Ok(await (from item in dbContext.Seasons.AsNoTracking()
            select new ShowSeasonResponse(item)).ToListAsync());
    }

    [HttpGet("{id}")]
    [ProducesResponseType<ShowSeasonResponse>(200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> GetSeason([FromRoute] int id)
    {
        ShowSeasonResponse? response = await (from item in dbContext.Seasons.AsNoTracking()
                .Include(s => s.Episodes
                    .OrderBy(e => e.EpisodeNumber))
            where item.Id == id
            select new ShowSeasonResponse(item)).FirstOrDefaultAsync();

        if (response is null)
        {
            return NotFound();
        }

        return Ok(response);
    }
}
