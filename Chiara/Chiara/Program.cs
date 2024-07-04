using Chiara.Extensions;
using Chiara.Services;
using Microsoft.EntityFrameworkCore;

WebApplicationBuilder builder = WebApplication.CreateBuilder();

builder.AddChiaraOptions();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();

builder.Services.AddDbContext<ChiaraDbContext>(options =>
{
    string? connectionString = builder.Configuration.GetConnectionString("Postgres");
    if (connectionString is null)
    {
        throw new InvalidOperationException("Failed to get postgres connection string.");
    }

    options.UseNpgsql(connectionString);
});
builder.Services.AddChiaraService();

WebApplication application = builder.Build();

if (application.Environment.IsDevelopment())
{
    application.UseSwagger();
    application.UseSwaggerUI();
}

application.UseStaticFiles();

application.MapControllers();

await application.RunAsync();
