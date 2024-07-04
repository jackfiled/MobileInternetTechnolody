using Chiara.Abstractions;
using Microsoft.AspNetCore.Mvc;

namespace Chiara.Controllers;

[Route("api/[controller]")]
[ApiController]
public class FileController(IFileStore fileStore) : ControllerBase
{
    [HttpGet("{key}")]
    [ProducesResponseType<FileStreamResult>(200)]
    public async Task<IActionResult> DownloadFile([FromRoute] string key)
    {
        if (!Guid.TryParse(key, out Guid guid))
        {
            return BadRequest();
        }

        IFile? file = await fileStore.GetFileAsync(guid);

        if (file is null)
        {
            return NotFound();
        }

        return File(file.OpenRead(), file.ContentType);
    }
}
