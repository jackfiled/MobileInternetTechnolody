using Chiara.DataTransferObjects;
using Chiara.Models;
using Chiara.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Chiara.Controllers;

[Route("api/[controller]")]
[ApiController]
public class SongController(ChiaraDbContext dbContext) : ControllerBase
{
    [HttpGet]
    [ProducesResponseType<IEnumerable<SongResponse>>(200)]
    public IActionResult ListSongs()
    {
        IEnumerable<SongResponse> response = from item in dbContext.Songs.AsNoTracking()
            select new SongResponse(item);

        return Ok(response);
    }

    [HttpGet("{id}")]
    [ProducesResponseType<SongResponse>(200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> GetSong([FromRoute] int id)
    {
        Song? song = await (from item in dbContext.Songs.AsNoTracking()
            where item.Id == id
            select item).FirstOrDefaultAsync();

        if (song is null)
        {
            return NotFound();
        }

        return Ok(new SongResponse(song));
    }
}
