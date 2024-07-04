using System.Text.Json;
using System.Text.Json.Serialization;
using AnitomySharp;

namespace Chiara.Tests.AnitomySharpTests;

public class TestCase
{
    [JsonPropertyName("file_name")]
    public string FileName { get; init; } = string.Empty;

    [JsonPropertyName("ignore")]
    public bool Ignore { get; init; }

    [JsonPropertyName("results")]
    public Dictionary<string, JsonElement> Results { get; init; } = [];
}

public class DataTests(ITestOutputHelper helper)
{
    [Fact]
    public void ValidateParsingResults()
    {
        string path = Path.Combine(Environment.CurrentDirectory, "test-cases.json");
        List<TestCase>? cases = JsonSerializer.Deserialize<List<TestCase>>(File.ReadAllText(path));
        Assert.NotNull(cases);

        helper.WriteLine($"Loaded {cases.Count} test cases.");
        foreach (var testCase in cases)
        {
            Verify(testCase);
        }
    }

    private void Verify(TestCase entry)
    {
        string fileName = entry.FileName;
        bool ignore = entry.Ignore;
        Dictionary<string, JsonElement> testCases = entry.Results;

        if (ignore || string.IsNullOrWhiteSpace(fileName) || testCases.Count == 0)
        {
            helper.WriteLine($"Ignoring [{fileName}] : {{ results: {testCases.Count} | explicit: {ignore}}}");
            return;
        }

        helper.WriteLine($"Parsing: {fileName}");
        Dictionary<string, List<string>> parseResults = ToTestCaseDict(fileName);

        foreach (KeyValuePair<string,JsonElement> pair in testCases)
        {
            Assert.True(parseResults.TryGetValue(pair.Key, out List<string>? value));
            Assert.NotNull(value);

            if (pair.Value.ValueKind == JsonValueKind.String)
            {
                Assert.Contains(pair.Value.GetString(), value);
            }

            if (pair.Value.ValueKind != JsonValueKind.Array)
            {
                continue;
            }

            List<string>? exceptedArray = pair.Value.Deserialize<List<string>>();
            Assert.NotNull(exceptedArray);

            Assert.Equal(exceptedArray, value);
        }
    }

    private static Dictionary<string, List<string>> ToTestCaseDict(string filename)
    {
        var parseResults = Anitomy.Parse(filename);
        var elements = new Dictionary<string, List<string>>();

        foreach (var e in parseResults)
        {
            if (elements.TryGetValue(e.Category.ToString(), out List<string>? value))
            {
                value.Add(e.Value);
            }
            else
            {
                elements.Add(e.Category.ToString(), [e.Value]);
            }
        }

        return elements;
    }
}
