/*
 * Copyright (c) 2014-2017, Eren Okka
 * Copyright (c) 2016-2017, Paul Miller
 * Copyright (c) 2017-2018, Tyler Bratton
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

using System.Text.RegularExpressions;

namespace AnitomySharp;

/// <summary>
/// A utility class to assist in number parsing.
/// </summary>
public partial class ParserNumber(Parser parser)
{
    public const int AnimeYearMin = 1900;
    public const int AnimeYearMax = 2100;
    private const int EpisodeNumberMax = AnimeYearMax - 1;
    private const int VolumeNumberMax = 50;
    private const string RegexMatchOnlyStart = @"\A(?:";
    private const string RegexMatchOnlyEnd = @")\z";

    /// <summary>
    /// Returns whether or not the <code>number</code> is a volume number
    /// </summary>
    private static bool IsValidVolumeNumber(string number)
    {
        return StringHelper.StringToInt(number) <= VolumeNumberMax;
    }

    /// <summary>
    /// Returns whether or not the <code>number</code> is a valid episode number.
    /// </summary>
    private static bool IsValidEpisodeNumber(string number)
    {
        // Eliminate non numeric portion of number, then parse as double.
        string temp = "";
        for (int i = 0; i < number.Length && char.IsDigit(number[i]); i++)
        {
            temp += number[i];
        }

        return !string.IsNullOrEmpty(temp) && double.Parse(temp) <= EpisodeNumberMax;
    }

    /// <summary>
    /// Sets the alternative episode number.
    /// </summary>
    private void SetAlternativeEpisodeNumber(string number, Token token)
    {
        parser.Elements.Add(new Element(ElementCategory.ElementEpisodeNumberAlt, number));
        token.Category = Token.TokenCategory.Identifier;
    }

    /// <summary>
    /// Sets the volume number.
    /// </summary>
    /// <param name="number">the number</param>
    /// <param name="token">the token which contains the volume number</param>
    /// <param name="validate">true if we should check if it's a valid number, false to disable verification</param>
    /// <returns>true if the volume number was set</returns>
    public bool SetVolumeNumber(string number, Token token, bool validate)
    {
        if (validate && !IsValidVolumeNumber(number)) return false;

        parser.Elements.Add(new Element(ElementCategory.ElementVolumeNumber, number));
        token.Category = Token.TokenCategory.Identifier;
        return true;
    }

    /// <summary>
    /// Sets the anime episode number.
    /// </summary>
    /// <param name="number">the episode number</param>
    /// <param name="token">the token which contains the volume number</param>
    /// <param name="validate">true if we should check if it's a valid episode number; false to disable validation</param>
    /// <returns>true if the episode number was set</returns>
    public bool SetEpisodeNumber(string number, Token token, bool validate)
    {
        if (validate && !IsValidEpisodeNumber(number)) return false;
        token.Category = Token.TokenCategory.Identifier;
        var category = ElementCategory.ElementEpisodeNumber;

        if (parser.IsEpisodeKeywordsFound)
        {
            foreach (var element in parser.Elements)
            {
                if (element.Category != ElementCategory.ElementEpisodeNumber) continue;

                int comparison = StringHelper.StringToInt(number) - StringHelper.StringToInt(element.Value);
                switch (comparison)
                {
                    case > 0:
                        category = ElementCategory.ElementEpisodeNumberAlt;
                        break;
                    case < 0:
                        element.Category = ElementCategory.ElementEpisodeNumberAlt;
                        break;
                    default:
                        return false;
                }

                break;
            }
        }

        parser.Elements.Add(new Element(category, number));
        return true;
    }

    /// <summary>
    /// Checks if a number follows the specified <code>token</code>
    /// </summary>
    /// <param name="category">the category to set if a number follows the <code>token</code></param>
    /// <param name="token">the token</param>
    /// <returns>true if a number follows the token; false otherwise</returns>
    private bool NumberComesAfterPrefix(ElementCategory category, Token token)
    {
        int numberBegin = ParserHelper.IndexOfFirstDigit(token.Content);
        string prefix = StringHelper.SubstringWithCheck(token.Content, 0, numberBegin).ToUpperInvariant();
        if (!KeywordManager.Contains(category, prefix)) return false;
        string number = StringHelper.SubstringWithCheck(token.Content, numberBegin, token.Content.Length - numberBegin);

        switch (category)
        {
            case ElementCategory.ElementEpisodePrefix:
                if (!MatchEpisodePatterns(number, token))
                {
                    SetEpisodeNumber(number, token, false);
                }
                return true;
            case ElementCategory.ElementVolumePrefix:
                if (!MatchVolumePatterns(number, token))
                {
                    SetVolumeNumber(number, token, false);
                }
                return true;
            default:
                return false;
        }
    }

    /// <summary>
    /// Checks whether the number precedes the word "of"
    /// </summary>
    /// <param name="token">the token</param>
    /// <param name="currentTokenIdx">the index of the token</param>
    /// <returns>true if the token precedes the word "of"</returns>
    private bool NumberComesBeforeAnotherNumber(Token token, int currentTokenIdx)
    {
        int separatorToken = Token.FindNextToken(parser.Tokens, currentTokenIdx, Token.TokenFlag.FlagNotDelimiter);

        if (!Token.InListRange(separatorToken, parser.Tokens)) return false;
        var separators = new List<Tuple<string, bool>>
        {
            Tuple.Create("&", true),
            Tuple.Create("of", false)
        };

        foreach (var separator in separators)
        {
            if (parser.Tokens[separatorToken].Content != separator.Item1) continue;
            int otherToken = Token.FindNextToken(parser.Tokens, separatorToken, Token.TokenFlag.FlagNotDelimiter);
            if (!Token.InListRange(otherToken, parser.Tokens)
                || !StringHelper.IsNumericString(parser.Tokens[otherToken].Content)) continue;
            SetEpisodeNumber(token.Content, token, false);
            if (separator.Item2)
            {
                SetEpisodeNumber(parser.Tokens[otherToken].Content, parser.Tokens[otherToken], false);
            }

            parser.Tokens[separatorToken].Category = Token.TokenCategory.Identifier;
            parser.Tokens[otherToken].Category = Token.TokenCategory.Identifier;
            return true;
        }

        return false;
    }

    // EPISODE MATCHERS

    /// <summary>
    /// Attempts to find an episode/season inside a <code>word</code>
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the word was matched to an episode/season number</returns>
    public bool MatchEpisodePatterns(string word, Token token)
    {
        if (StringHelper.IsNumericString(word)) return false;

        word = word.Trim(" -".ToCharArray());

        bool numericFront = char.IsDigit(word[0]);
        bool numericBack = char.IsDigit(word[^1]);

        if (numericFront && numericBack)
        {
            // e.g. "01v2"
            if (MatchSingleEpisodePattern(word, token))
            {
                return true;
            }
            // e.g. "01-02", "03-05v2"

            if (MatchMultiEpisodePattern(word, token))
            {
                return true;
            }
            // e.g. "07.5"

            if (MatchFractionalEpisodePattern(word, token))
            {
                return true;
            }
        }

        if (numericBack)
        {
            // e.g. "2x01", "S01E03", "S01-02xE001-150"
            if (MatchSeasonAndEpisodePattern(word, token))
            {
                return true;
            }
            // e.g. "#01", "#02-03v2"

            if (MatchNumberSignPattern(word, token))
            {
                return true;
            }
        }

        switch (numericFront)
        {
            // e.g. "ED1", "OP4a", "OVA2"
            case false when MatchTypeAndEpisodePattern(word, token):
            // e.g. "4a", "111C"
            case true when !numericBack && MatchPartialEpisodePattern(word, token):
                return true;
            default:
                // U+8A71 is used as counter for stories, episodes of TV series, etc.
                return numericFront && MatchJapaneseCounterPattern(word, token);
        }
    }

    /// <summary>
    /// Match a single episode pattern. e.g. "01v2".
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchSingleEpisodePattern(string word, Token token)
    {
        var match = SingleEpisodeRegex().Match(word);

        if (!match.Success) return false;

        SetEpisodeNumber(match.Groups[1].Value, token, false);
        parser.Elements.Add(new Element(ElementCategory.ElementReleaseVersion, match.Groups[2].Value));
        return true;
    }

    /// <summary>
    /// Match a multi episode pattern. e.g. "01-02", "03-05v2".
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchMultiEpisodePattern(string word, Token token)
    {
        var match = MultiEpisodeRegex().Match(word);
        if (!match.Success) return false;

        string lowerBound = match.Groups[1].Value;
        string upperBound = match.Groups[3].Value;

        if (StringHelper.StringToInt(lowerBound) >= StringHelper.StringToInt(upperBound)) return false;
        if (!SetEpisodeNumber(lowerBound, token, true)) return false;
        SetEpisodeNumber(upperBound, token, true);
        if (!string.IsNullOrEmpty(match.Groups[2].Value))
        {
            parser.Elements.Add(new Element(ElementCategory.ElementReleaseVersion, match.Groups[2].Value));
        }
        if (!string.IsNullOrEmpty(match.Groups[4].Value))
        {
            parser.Elements.Add(new Element(ElementCategory.ElementReleaseVersion, match.Groups[4].Value));
        }
        return true;

    }

    /// <summary>
    /// Match season and episode patterns. e.g. "2x01", "S01E03", "S01-02xE001-150".
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchSeasonAndEpisodePattern(string word, Token token)
    {
        var match = SeasonEpisodeRegex().Match(word);
        if (!match.Success) return false;

        parser.Elements.Add(new Element(ElementCategory.ElementAnimeSeason, match.Groups[1].Value));
        if (!string.IsNullOrEmpty(match.Groups[2].Value))
        {
            parser.Elements.Add(new Element(ElementCategory.ElementAnimeSeason, match.Groups[2].Value));
        }
        SetEpisodeNumber(match.Groups[3].Value, token, false);
        if (!string.IsNullOrEmpty(match.Groups[4].Value))
        {
            SetEpisodeNumber(match.Groups[4].Value, token, false);
        }
        return true;
    }

    /// <summary>
    /// Match type and episode. e.g. "ED1", "OP4a", "OVA2".
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchTypeAndEpisodePattern(string word, Token token)
    {
        int numberBegin = ParserHelper.IndexOfFirstDigit(word);
        string prefix = StringHelper.SubstringWithCheck(word, 0, numberBegin);

        var category = ElementCategory.ElementAnimeType;
        var options = new KeywordOptions();

        if (!KeywordManager.FindAndSet(KeywordManager.Normalize(prefix), ref category, ref options)) return false;
        parser.Elements.Add(new Element(ElementCategory.ElementAnimeType, prefix));
        string number = word.Substring(numberBegin);
        if (!MatchEpisodePatterns(number, token) && !SetEpisodeNumber(number, token, true)) return false;
        int foundIdx = parser.Tokens.IndexOf(token);
        if (foundIdx == -1) return true;
        token.Content = number;
        parser.Tokens.Insert(foundIdx,
            new Token(options.Identifiable ? Token.TokenCategory.Identifier : Token.TokenCategory.Unknown, prefix, token.Enclosed));

        return true;

    }

    /// <summary>
    /// Match fractional episodes. e.g. "07.5"
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchFractionalEpisodePattern(string word, Token token)
    {
        if (string.IsNullOrEmpty(word))
        {
            word = "";
        }

        const string regexPattern = RegexMatchOnlyStart + @"\d+\.5" + RegexMatchOnlyEnd;
        var match = Regex.Match(word, regexPattern);
        return match.Success && SetEpisodeNumber(word, token, true);
    }

    /// <summary>
    /// Match partial episodes. e.g. "4a", "111C".
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchPartialEpisodePattern(string word, Token token)
    {
        if (string.IsNullOrEmpty(word)) return false;
        int foundIdx = Enumerable.Range(0, word.Length)
            .DefaultIfEmpty(word.Length)
            .FirstOrDefault(value => !char.IsDigit(word[value]));
        int suffixLength = word.Length - foundIdx;

        return suffixLength == 1 && IsValidSuffix(word[foundIdx]) && SetEpisodeNumber(word, token, true);

        bool IsValidSuffix(int c) => c is >= 'A' and <= 'C' or >= 'a' and <= 'c';
    }

    /// <summary>
    /// Match episodes with number signs. e.g. "#01", "#02-03v2"
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchNumberSignPattern(string word, Token token)
    {
        if (string.IsNullOrEmpty(word) || word[0] != '#') word = "";
        var match = NumberEpisodeRegex().Match(word);
        if (!match.Success) return false;

        if (!SetEpisodeNumber(match.Groups[1].Value, token, true)) return false;
        if (!string.IsNullOrEmpty(match.Groups[2].Value))
        {
            SetEpisodeNumber(match.Groups[2].Value, token, false);
        }
        if (!string.IsNullOrEmpty(match.Groups[3].Value))
        {
            parser.Elements.Add(new Element(ElementCategory.ElementReleaseVersion, match.Groups[3].Value));
        }

        return true;

    }

    /// <summary>
    /// Match Japanese patterns. e.g. U+8A71 is used as counter for stories, episodes of TV series, etc.
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchJapaneseCounterPattern(string word, Token token)
    {
        if (string.IsNullOrEmpty(word) || word[^1] != '\u8A71') return false;
        var match = JapaneseEpisodeRegex().Match(word);
        if (!match.Success) return false;
        SetEpisodeNumber(match.Groups[1].Value, token, false);
        return true;

    }

    // VOLUME MATCHES

    /// <summary>
    /// Attempts to find an episode/season inside a <code>word</code>
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the word was matched to an episode/season number</returns>
    public bool MatchVolumePatterns(string word, Token token)
    {
        // All patterns contain at least one non-numeric character
        if (StringHelper.IsNumericString(word)) return false;

        word = word.Trim(" -".ToCharArray());

        bool numericFront = char.IsDigit(word[0]);
        bool numericBack = char.IsDigit(word[^1]);

        if (numericFront && numericBack)
        {
            // e.g. "01v2"                                    e.g. "01-02", "03-05v2"
            return MatchSingleVolumePattern(word, token) || MatchMultiVolumePattern(word, token);
        }

        return false;
    }

    /// <summary>
    /// Match single volume. e.g. "01v2"
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchSingleVolumePattern(string word, Token token)
    {
        if (string.IsNullOrEmpty(word)) word = "";
        var match = SingleVolumeRegex().Match(word);
        if (!match.Success) return false;

        SetVolumeNumber(match.Groups[1].Value, token, false);
        parser.Elements.Add(new Element(ElementCategory.ElementReleaseVersion, match.Groups[2].Value));
        return true;
    }

    /// <summary>
    /// Match multi-volume. e.g. "01-02", "03-05v2".
    /// </summary>
    /// <param name="word">the word</param>
    /// <param name="token">the token</param>
    /// <returns>true if the token matched</returns>
    private bool MatchMultiVolumePattern(string word, Token token)
    {
        if (string.IsNullOrEmpty(word)) word = "";
        var match = MultiVolumeRegex().Match(word);
        if (!match.Success) return false;

        string lowerBound = match.Groups[1].Value;
        string upperBound = match.Groups[2].Value;
        if (StringHelper.StringToInt(lowerBound) >= StringHelper.StringToInt(upperBound)) return false;
        if (!SetVolumeNumber(lowerBound, token, true)) return false;
        SetVolumeNumber(upperBound, token, false);
        if (string.IsNullOrEmpty(match.Groups[3].Value))
        {
            parser.Elements.Add(new Element(ElementCategory.ElementReleaseVersion, match.Groups[3].Value));
        }
        return true;

    }

    // SEARCH

    /// <summary>
    /// Searches for isolated numbers in a list of <code>tokens</code>.
    /// </summary>
    /// <param name="tokens">the list of tokens</param>
    /// <returns>true if an isolated number was found</returns>
    public bool SearchForIsolatedNumbers(IEnumerable<int> tokens)
    {
        return tokens
            .Where(it => parser.Tokens[it].Enclosed && parser.ParseHelper.IsTokenIsolated(it))
            .Any(it => SetEpisodeNumber(parser.Tokens[it].Content, parser.Tokens[it], true));
    }

    /// <summary>
    /// Searches for separated numbers in a list of <code>tokens</code>.
    /// </summary>
    /// <param name="tokens">the list of tokens</param>
    /// <returns>true fi a separated number was found</returns>
    public bool SearchForSeparatedNumbers(List<int> tokens)
    {
        foreach (int it in tokens)
        {
            int previousToken = Token.FindPrevToken(parser.Tokens, it, Token.TokenFlag.FlagNotDelimiter);

            // See if the number has a preceding "-" separator
            if (!parser.ParseHelper.IsTokenCategory(previousToken, Token.TokenCategory.Unknown)
                || !ParserHelper.IsDashCharacter(parser.Tokens[previousToken].Content[0])) continue;
            if (!SetEpisodeNumber(parser.Tokens[it].Content, parser.Tokens[it], true)) continue;
            parser.Tokens[previousToken].Category = Token.TokenCategory.Identifier;
            return true;
        }

        return false;
    }

    /// <summary>
    /// Searches for episode patterns in a list of <code>tokens</code>.
    /// </summary>
    /// <param name="tokens">the list of tokens</param>
    /// <returns>true if an episode number was found</returns>
    public bool SearchForEpisodePatterns(List<int> tokens)
    {
        foreach (int it in tokens)
        {
            bool numericFront = parser.Tokens[it].Content.Length > 0 && char.IsDigit(parser.Tokens[it].Content[0]);

            if (!numericFront)
            {
                // e.g. "EP.1", "Vol.1"
                if (NumberComesAfterPrefix(ElementCategory.ElementEpisodePrefix, parser.Tokens[it]))
                {
                    return true;
                }
                if (NumberComesAfterPrefix(ElementCategory.ElementVolumePrefix, parser.Tokens[it]))
                {
                    continue;
                }
            }
            else
            {
                // e.g. "8 & 10", "01 of 24"
                if (NumberComesBeforeAnotherNumber(parser.Tokens[it], it))
                {
                    return true;
                }
            }

            // Look for other patterns
            if (MatchEpisodePatterns(parser.Tokens[it].Content, parser.Tokens[it]))
            {
                return true;
            }
        }

        return false;
    }

    /// <summary>
    /// Searches for equivalent number in a list of <code>tokens</code>. e.g. 08(114)
    /// </summary>
    /// <param name="tokens">the list of tokens</param>
    /// <returns>true if an equivalent number was found</returns>
    public bool SearchForEquivalentNumbers(List<int> tokens)
    {
        foreach (int it in tokens)
        {
            // Find number must be isolated.
            if (parser.ParseHelper.IsTokenIsolated(it) || !IsValidEpisodeNumber(parser.Tokens[it].Content))
            {
                continue;
            }

            // Find the first enclosed, non-delimiter token
            int nextToken = Token.FindNextToken(parser.Tokens, it, Token.TokenFlag.FlagNotDelimiter);
            if (!parser.ParseHelper.IsTokenCategory(nextToken, Token.TokenCategory.Bracket)) continue;
            nextToken = Token.FindNextToken(parser.Tokens, nextToken, Token.TokenFlag.FlagEnclosed,
                Token.TokenFlag.FlagNotDelimiter);
            if (!parser.ParseHelper.IsTokenCategory(nextToken, Token.TokenCategory.Unknown)) continue;

            // Check if it's an isolated number
            if (!parser.ParseHelper.IsTokenIsolated(nextToken)
                || !StringHelper.IsNumericString(parser.Tokens[nextToken].Content)
                || !IsValidEpisodeNumber(parser.Tokens[nextToken].Content))
            {
                continue;
            }

            var list = new List<Token>
            {
                parser.Tokens[it], parser.Tokens[nextToken]
            };

            list.Sort((o1, o2) => StringHelper.StringToInt(o1.Content) - StringHelper.StringToInt(o2.Content));
            SetEpisodeNumber(list[0].Content, list[0], false);
            SetAlternativeEpisodeNumber(list[1].Content, list[1]);
            return true;
        }

        return false;
    }

    /// <summary>
    /// Searches for the last number token in a list of <code>tokens</code>
    /// </summary>
    /// <param name="tokens">the list of tokens</param>
    /// <returns>true if the last number token was found</returns>
    public bool SearchForLastNumber(List<int> tokens)
    {
        for (int i = tokens.Count - 1; i >= 0; i--)
        {
            int it = tokens[i];

            // Assuming that episode number always comes after the title,
            // the first token cannot be what we're looking for
            if (it == 0) continue;
            if (parser.Tokens[it].Enclosed) continue;

            // Ignore if it's the first non-enclosed, non-delimiter token
            if (parser.Tokens.GetRange(0, it)
                .All(r => r.Enclosed || r.Category == Token.TokenCategory.Delimiter))
            {
                continue;
            }

            int previousToken = Token.FindPrevToken(parser.Tokens, it, Token.TokenFlag.FlagNotDelimiter);
            if (parser.ParseHelper.IsTokenCategory(previousToken, Token.TokenCategory.Unknown))
            {
                if (parser.Tokens[previousToken].Content.Equals("Movie", StringComparison.InvariantCultureIgnoreCase)
                    || parser.Tokens[previousToken].Content.Equals("Part", StringComparison.InvariantCultureIgnoreCase))
                {
                    continue;
                }
            }

            // We'll use this number after all
            if (SetEpisodeNumber(parser.Tokens[it].Content, parser.Tokens[it], true))
            {
                return true;
            }
        }

        return false;
    }

    [GeneratedRegex(@"\A(?:(\d{1,3})[vV](\d))\z")]
    private static partial Regex SingleEpisodeRegex();

    [GeneratedRegex(@"\A(?:(\d{1,3})(?:[vV](\d))?[-~&+](\d{1,3})(?:[vV](\d))?)\z")]
    private static partial Regex MultiEpisodeRegex();

    [GeneratedRegex(@"\A(?:S?(\d{1,2})(?:-S?(\d{1,2}))?(?:x|[ ._-x]?E)(\d{1,3})(?:-E?(\d{1,3}))?)\z")]
    private static partial Regex SeasonEpisodeRegex();

    [GeneratedRegex(@"\A(?:#(\d{1,3})(?:[-~&+](\d{1,3}))?(?:[vV](\d))?)\z")]
    private static partial Regex NumberEpisodeRegex();

    [GeneratedRegex(@"\A(?:(\d{1,3})話)\z")]
    private static partial Regex JapaneseEpisodeRegex();

    [GeneratedRegex(@"\A(?:(\d{1,2})[vV](\d))\z")]
    private static partial Regex SingleVolumeRegex();

    [GeneratedRegex(@"\A(?:(\d{1,2})[-~&+](\d{1,2})(?:[vV](\d))?)\z")]
    private static partial Regex MultiVolumeRegex();
}
