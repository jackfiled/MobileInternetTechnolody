using Chiara.DataTransferObjects;
using Chiara.Models;
using Chiara.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Chiara.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AlbumController(ChiaraDbContext dbContext) : ControllerBase
{
    [HttpGet]
    [ProducesResponseType<IEnumerable<AlbumResponse>>(200)]
    public async Task<IActionResult> GetAllAlbums()
    {
        IQueryable<AlbumResponse> albumResponses = from item in dbContext.Albums.AsNoTracking()
            select new AlbumResponse(item);

        return Ok(await albumResponses.ToListAsync());
    }

    [HttpGet("{id}")]
    [ProducesResponseType<AlbumResponse>(200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> GetAlbum([FromRoute] int id)
    {
        Album? album = await (from item in dbContext.Albums.AsNoTracking()
                .Include(a => a.Songs)
            where item.Id == id
            select item).FirstOrDefaultAsync();

        if (album is null)
        {
            return NotFound();
        }

        return Ok(new AlbumResponse(album));
    }
}
