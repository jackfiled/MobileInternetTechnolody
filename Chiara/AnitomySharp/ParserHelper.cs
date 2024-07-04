/*
 * Copyright (c) 2014-2017, Eren Okka
 * Copyright (c) 2016-2017, Paul Miller
 * Copyright (c) 2017-2018, Tyler Bratton
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

using System.Text;

namespace AnitomySharp;

/// <summary>
/// Utility class to assist in the parsing.
/// </summary>
public class ParserHelper(Parser parser)
{
    private static readonly System.Buffers.SearchValues<char> s_myChars = System.Buffers.SearchValues.Create("xX\u00D7");
    private const string Dashes = "-\u2010\u2011\u2012\u2013\u2014\u2015";
    private const string DashesWithSpace = " -\u2010\u2011\u2012\u2013\u2014\u2015";

    private static readonly Dictionary<string, string> s_ordinals = new()
    {
        {"1st", "1"}, {"First", "1"},
        {"2nd", "2"}, {"Second", "2"},
        {"3rd", "3"}, {"Third", "3"},
        {"4th", "4"}, {"Fourth", "4"},
        {"5th", "5"}, {"Fifth", "5"},
        {"6th", "6"}, {"Sixth", "6"},
        {"7th", "7"}, {"Seventh", "7"},
        {"8th", "8"}, {"Eighth", "8"},
        {"9th", "9"}, {"Ninth", "9"}
    };

    /// <summary>
    /// Returns whether or not the <code>result</code> matches the <code>category</code>.
    /// </summary>
    public bool IsTokenCategory(int result, Token.TokenCategory category)
    {
        return Token.InListRange(result, parser.Tokens) && parser.Tokens[result].Category == category;
    }

    /// <summary>
    /// Returns whether or not the <code>str</code> is a CRC string.
    /// </summary>
    public static bool IsCrc32(string str)
    {
        return str.Length == 8 && StringHelper.IsHexadecimalString(str);
    }

    /// <summary>
    /// Returns whether or not the <code>character</code> is a dash character
    /// </summary>
    public static bool IsDashCharacter(char c)
    {
        return Dashes.Contains(c.ToString());
    }

    /// <summary>
    /// Returns a number from an original (e.g. 2nd)
    /// </summary>
    private static string GetNumberFromOrdinal(string str)
    {
        return string.IsNullOrEmpty(str) ? string.Empty : s_ordinals.GetValueOrDefault(str, "");
    }

    /// <summary>
    /// Returns the index of the first digit in the <code>str</code>; -1 otherwise.
    /// </summary>
    public static int IndexOfFirstDigit(string str)
    {
        if (string.IsNullOrEmpty(str)) return -1;
        for (int i = 0; i < str.Length; i++)
        {
            if (char.IsDigit(str, i))
            {
                return i;
            }
        }
        return -1;
    }

    /// <summary>
    /// Returns whether or not the <code>str</code> is a resolution.
    /// </summary>
    public static bool IsResolution(string str)
    {
        if (string.IsNullOrEmpty(str)) return false;
        const int minWidthSize = 3;
        const int minHeightSize = 3;

        switch (str.Length)
        {
            case >= minWidthSize + 1 + minHeightSize:
                {
                    int pos = str.AsSpan().IndexOfAny(s_myChars);
                    if (pos == -1 || pos < minWidthSize || pos > str.Length - (minHeightSize + 1)) return false;
                    return !str.Where((t, i) => i != pos && !char.IsDigit(t)).Any();
                }
            case < minHeightSize + 1:
                return false;
            default:
                {
                    if (char.ToLower(str[^1]) != 'p') return false;
                    for (int i = 0; i < str.Length - 1; i++)
                    {
                        if (!char.IsDigit(str[i])) return false;
                    }

                    return true;
                }
        }
    }

    /// <summary>
    /// Returns whether or not the <code>category</code> is searchable.
    /// </summary>
    public static bool IsElementCategorySearchable(ElementCategory category)
    {
        return category switch
        {
            ElementCategory.ElementAnimeSeasonPrefix or ElementCategory.ElementAnimeType
                or ElementCategory.ElementAudioTerm or ElementCategory.ElementDeviceCompatibility
                or ElementCategory.ElementEpisodePrefix or ElementCategory.ElementFileChecksum
                or ElementCategory.ElementLanguage or ElementCategory.ElementOther
                or ElementCategory.ElementReleaseGroup or ElementCategory.ElementReleaseInformation
                or ElementCategory.ElementReleaseVersion or ElementCategory.ElementSource
                or ElementCategory.ElementSubtitles or ElementCategory.ElementVideoResolution
                or ElementCategory.ElementVideoTerm or ElementCategory.ElementVolumePrefix => true,
            _ => false
        };
    }

    /// <summary>
    /// Returns whether the <code>category</code> is singular.
    /// </summary>
    public static bool IsElementCategorySingular(ElementCategory category)
    {
        return category switch
        {
            ElementCategory.ElementAnimeSeason or ElementCategory.ElementAnimeType or ElementCategory.ElementAudioTerm
                or ElementCategory.ElementDeviceCompatibility or ElementCategory.ElementEpisodeNumber
                or ElementCategory.ElementLanguage or ElementCategory.ElementOther
                or ElementCategory.ElementReleaseInformation or ElementCategory.ElementSource
                or ElementCategory.ElementVideoTerm => false,
            _ => false
        };
    }

    /// <summary>
    /// Returns whether or not a token at the current <code>pos</code> is isolated(surrounded by braces).
    /// </summary>
    public bool IsTokenIsolated(int pos)
    {
        int prevToken = Token.FindPrevToken(parser.Tokens, pos, Token.TokenFlag.FlagNotDelimiter);
        if (!IsTokenCategory(prevToken, Token.TokenCategory.Bracket)) return false;
        int nextToken = Token.FindNextToken(parser.Tokens, pos, Token.TokenFlag.FlagNotDelimiter);
        return IsTokenCategory(nextToken, Token.TokenCategory.Bracket);
    }

    /// <summary>
    /// Finds and sets the anime season keyword.
    /// </summary>
    public bool CheckAndSetAnimeSeasonKeyword(Token token, int currentTokenPos)
    {
        int previousToken = Token.FindPrevToken(parser.Tokens, currentTokenPos, Token.TokenFlag.FlagNotDelimiter);
        if (Token.InListRange(previousToken, parser.Tokens))
        {
            string number = GetNumberFromOrdinal(parser.Tokens[previousToken].Content);
            if (!string.IsNullOrEmpty(number))
            {
                SetAnimeSeason(parser.Tokens[previousToken], token, number);
                return true;
            }
        }

        int nextToken = Token.FindNextToken(parser.Tokens, currentTokenPos, Token.TokenFlag.FlagNotDelimiter);
        if (!Token.InListRange(nextToken, parser.Tokens) ||
            !StringHelper.IsNumericString(parser.Tokens[nextToken].Content)) return false;
        SetAnimeSeason(token, parser.Tokens[nextToken], parser.Tokens[nextToken].Content);
        return true;

        void SetAnimeSeason(Token first, Token second, string content)
        {
            parser.Elements.Add(new Element(ElementCategory.ElementAnimeSeason, content));
            first.Category = Token.TokenCategory.Identifier;
            second.Category = Token.TokenCategory.Identifier;
        }
    }

    /// <summary>
    /// A Method to find the correct volume/episode number when prefixed (i.e. Vol.4).
    /// </summary>
    /// <param name="category">the category we're searching for</param>
    /// <param name="currentTokenPos">the current token position</param>
    /// <param name="token">the token</param>
    /// <returns>true if we found the volume/episode number</returns>
    public bool CheckExtentKeyword(ElementCategory category, int currentTokenPos, Token token)
    {
        int nToken = Token.FindNextToken(parser.Tokens, currentTokenPos, Token.TokenFlag.FlagNotDelimiter);
        if (!IsTokenCategory(nToken, Token.TokenCategory.Unknown)) return false;
        if (IndexOfFirstDigit(parser.Tokens[nToken].Content) != 0) return false;
        switch (category)
        {
            case ElementCategory.ElementEpisodeNumber:
                if (!parser.ParseNumber.MatchEpisodePatterns(parser.Tokens[nToken].Content, parser.Tokens[nToken]))
                {
                    parser.ParseNumber.SetEpisodeNumber(parser.Tokens[nToken].Content, parser.Tokens[nToken], false);
                }
                break;
            case ElementCategory.ElementVolumeNumber:
                if (!parser.ParseNumber.MatchVolumePatterns(parser.Tokens[nToken].Content, parser.Tokens[nToken]))
                {
                    parser.ParseNumber.SetVolumeNumber(parser.Tokens[nToken].Content, parser.Tokens[nToken], false);
                }
                break;
        }

        token.Category = Token.TokenCategory.Identifier;
        return true;

    }


    public void BuildElement(ElementCategory category, bool keepDelimiters, List<Token> tokens)
    {
        var element = new StringBuilder();

        for (int i = 0; i < tokens.Count; i++)
        {
            var token = tokens[i];
            switch (token.Category)
            {
                case Token.TokenCategory.Unknown:
                    element.Append(token.Content);
                    token.Category = Token.TokenCategory.Identifier;
                    break;
                case Token.TokenCategory.Bracket:
                    element.Append(token.Content);
                    break;
                case Token.TokenCategory.Delimiter:
                    string delimiter = "";
                    if (!string.IsNullOrEmpty(token.Content))
                    {
                        delimiter = token.Content[0].ToString();
                    }

                    if (keepDelimiters)
                    {
                        element.Append(delimiter);
                    }
                    else if (Token.InListRange(i, tokens))
                    {
                        switch (delimiter)
                        {
                            case ",":
                            case "&":
                                element.Append(delimiter);
                                break;
                            default:
                                element.Append(' ');
                                break;
                        }
                    }
                    break;
            }
        }

        if (!keepDelimiters)
        {
            element = new StringBuilder(element.ToString().Trim(DashesWithSpace.ToCharArray()));
        }

        if (!string.IsNullOrEmpty(element.ToString()))
        {
            parser.Elements.Add(new Element(category, element.ToString()));
        }
    }
}
