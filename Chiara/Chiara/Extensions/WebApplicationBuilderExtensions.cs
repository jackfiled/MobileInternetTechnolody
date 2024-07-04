using Chiara.Models;

namespace Chiara.Extensions;

public static class WebApplicationBuilderExtensions
{
    public static WebApplicationBuilder AddChiaraOptions(this WebApplicationBuilder builder)
    {
        builder.Services.Configure<ChiaraOptions>(builder.Configuration.GetSection(ChiaraOptions.OptionName));
        return builder;
    }
}
