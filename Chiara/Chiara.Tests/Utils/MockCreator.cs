using Chiara.Services;
using Microsoft.Extensions.Logging;

namespace Chiara.Tests.Utils;

public static class MockCreator
{
    public static ILogger<T> CreateNoOutputLogger<T>()
    {
        Mock<ILogger<T>> mock = new();

        return mock.Object;
    }

    public static T CreateEmptyMock<T>() where T : class
    {
        Mock<T> mock = new();
        return mock.Object;
    }
}
