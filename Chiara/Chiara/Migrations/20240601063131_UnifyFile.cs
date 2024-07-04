using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Chiara.Migrations
{
    /// <inheritdoc />
    public partial class UnifyFile : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "ForeignId",
                table: "Files",
                newName: "FileId");

            migrationBuilder.RenameIndex(
                name: "IX_Files_ForeignId",
                table: "Files",
                newName: "IX_Files_FileId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "FileId",
                table: "Files",
                newName: "ForeignId");

            migrationBuilder.RenameIndex(
                name: "IX_Files_FileId",
                table: "Files",
                newName: "IX_Files_ForeignId");
        }
    }
}
