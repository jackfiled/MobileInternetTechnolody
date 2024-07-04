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
/// A class that will tokenize an anime filename.
/// </summary>
public class Tokenizer
{
    private readonly string _filename;
    private readonly List<Element> _elements;
    private readonly Options _options;
    private readonly List<Token> _tokens;
    private static readonly List<Tuple<string, string>> s_brackets =
    [
        new Tuple<string, string>("(", ")"), // U+0028-U+0029
        new Tuple<string, string>("[", "]"), // U+005B-U+005D Square bracket
        new Tuple<string, string>("{", "}"), // U+007B-U+007D Curly bracket
        new Tuple<string, string>("\u300C", "\u300D"), // Corner bracket
        new Tuple<string, string>("\u300E", "\u300E"), // White corner bracket
        new Tuple<string, string>("\u3010", "\u3011"), // Black lenticular bracket
        new Tuple<string, string>("\uFF08", "\uFF09")
    ];

    /// <summary>
    /// Tokenize a filename into <see cref="Element"/>s
    /// </summary>
    /// <param name="filename">the filename</param>
    /// <param name="elements">the list of elements where pre-identified tokens will be added</param>
    /// <param name="options">the parser options</param>
    /// <param name="tokens">the list of tokens where tokens will be added</param>
    public Tokenizer(string filename, List<Element> elements, Options options, List<Token> tokens)
    {
        _filename = filename;
        _elements = elements;
        _options = options;
        _tokens = tokens;
    }

    /// <summary>
    /// Returns true if tokenization was successful; false otherwise.
    /// </summary>
    /// <returns></returns>
    public bool Tokenize()
    {
        TokenizeByBrackets();
        return _tokens.Count > 0;
    }

    /// <summary>
    /// Adds a token to the internal list of tokens
    /// </summary>
    /// <param name="category">the token category</param>
    /// <param name="enclosed">whether or not the token is enclosed in braces</param>
    /// <param name="range">the token range</param>
    private void AddToken(Token.TokenCategory category, bool enclosed, TokenRange range)
    {
        _tokens.Add(new Token(category, StringHelper.SubstringWithCheck(_filename, range.Offset, range.Size), enclosed));
    }

    private string GetDelimiters(TokenRange range)
    {
        var delimiters = new StringBuilder();

        foreach (var i in Enumerable.Range(range.Offset, Math.Min(_filename.Length, range.Offset + range.Size) - range.Offset)
                     .Where(value => IsDelimiter(_filename[value])))
        {
            delimiters.Append(_filename[i]);
        }

        return delimiters.ToString();

        bool IsDelimiter(char c)
        {
            if (StringHelper.IsAlphanumericChar(c)) return false;
            return _options.AllowedDelimiters.Contains(c.ToString()) && !delimiters.ToString().Contains(c.ToString());
        }
    }

    /// <summary>
    /// Tokenize by bracket.
    /// </summary>
    private void TokenizeByBrackets()
    {
        string matchingBracket = string.Empty;

        bool isBracketOpen = false;
        for (int i = 0; i < _filename.Length; )
        {
            int foundIdx = !isBracketOpen ? FindFirstBracket(i, _filename.Length) : _filename.IndexOf(matchingBracket, i, StringComparison.Ordinal);

            var range = new TokenRange(i, foundIdx == -1 ? _filename.Length : foundIdx - i);
            if (range.Size > 0)
            {
                // Check if our range contains any known anime identifiers
                TokenizeByPreIdentified(isBracketOpen, range);
            }

            if (foundIdx != -1)
            {
                // mark as bracket
                AddToken(Token.TokenCategory.Bracket, true, new TokenRange(range.Offset + range.Size, 1));
                isBracketOpen = !isBracketOpen;
                i = foundIdx + 1;
            }
            else
            {
                break;
            }
        }

        return;

        int FindFirstBracket(int start, int end)
        {
            for (int i = start; i < end; i++)
            {
                foreach (var bracket in s_brackets)
                {
                    if (!_filename[i].Equals(char.Parse(bracket.Item1))) continue;
                    matchingBracket = bracket.Item2;
                    return i;
                }
            }

            return -1;
        }
    }

    /// <summary>
    /// Tokenize by looking for known anime identifiers
    /// </summary>
    /// <param name="enclosed">whether or not the current <code>range</code> is enclosed in braces</param>
    /// <param name="range">the token range</param>
    private void TokenizeByPreIdentified(bool enclosed, TokenRange range)
    {
        var preIdentifiedTokens = new List<TokenRange>();

        // Find known anime identifiers
        KeywordManager.PeekAndAdd(_filename, range, _elements, preIdentifiedTokens);

        int offset = range.Offset;
        var subRange = new TokenRange(range.Offset, 0);
        while (offset < range.Offset + range.Size)
        {
            foreach (var preIdentifiedToken in preIdentifiedTokens)
            {
                if (offset != preIdentifiedToken.Offset) continue;
                if (subRange.Size > 0)
                {
                    TokenizeByDelimiters(enclosed, subRange);
                }

                AddToken(Token.TokenCategory.Identifier, enclosed, preIdentifiedToken);
                subRange.Offset = preIdentifiedToken.Offset + preIdentifiedToken.Size;
                offset = subRange.Offset - 1; // It's going to be incremented below
            }

            subRange.Size = ++offset - subRange.Offset;
        }

        // Either there was no preidentified token range, or we're now about to process the tail of our current range
        if (subRange.Size > 0)
        {
            TokenizeByDelimiters(enclosed, subRange);
        }
    }

    /// <summary>
    /// Tokenize by delimiters allowed in <see cref="Options"/>.AllowedDelimiters.
    /// </summary>
    /// <param name="enclosed">whether or not the current <code>range</code> is enclosed in braces</param>
    /// <param name="range">the token range</param>
    private void TokenizeByDelimiters(bool enclosed, TokenRange range)
    {
        string delimiters = GetDelimiters(range);

        if (string.IsNullOrEmpty(delimiters))
        {
            AddToken(Token.TokenCategory.Unknown, enclosed, range);
            return;
        }

        for (int i = range.Offset, end = range.Offset + range.Size; i < end;)
        {
            int found = Enumerable.Range(i, Math.Min(end, _filename.Length) - i)
                .Where(c => delimiters.Contains(_filename[c].ToString()))
                .DefaultIfEmpty(end)
                .FirstOrDefault();

            var subRange = new TokenRange(i, found - i);
            if (subRange.Size > 0)
            {
                AddToken(Token.TokenCategory.Unknown, enclosed, subRange);
            }

            if (found != end)
            {
                AddToken(Token.TokenCategory.Delimiter, enclosed, new TokenRange(subRange.Offset + subRange.Size, 1));
                i = found + 1;
            }
            else
            {
                break;
            }
        }

        ValidateDelimiterTokens();
    }

    /// <summary>
    /// Validates tokens (make sure certain words delimited by certain tokens aren't split)
    /// </summary>
    private void ValidateDelimiterTokens()
    {
        for (int i = 0; i < _tokens.Count; i++)
        {
            var token = _tokens[i];
            if (token.Category != Token.TokenCategory.Delimiter) continue;
            char delimiter = token.Content[0];

            int prevToken = Token.FindPrevToken(_tokens, i, Token.TokenFlag.FlagValid);
            int nextToken = Token.FindNextToken(_tokens, i, Token.TokenFlag.FlagValid);

            // Check for single-character tokens to prevent splitting group names,
            // keywords, episode numbers, etc.
            if (delimiter != ' ' && delimiter != '_')
            {

                // Single character token
                if (IsSingleCharacterToken(prevToken))
                {
                    AppendTokenTo(token, _tokens[prevToken]);

                    while (IsUnknownToken(nextToken))
                    {
                        AppendTokenTo(_tokens[nextToken], _tokens[prevToken]);

                        nextToken = Token.FindNextToken(_tokens, i, Token.TokenFlag.FlagValid);
                        if (!IsDelimiterToken(nextToken) || _tokens[nextToken].Content[0] != delimiter) continue;
                        AppendTokenTo(_tokens[nextToken], _tokens[prevToken]);
                        nextToken = Token.FindNextToken(_tokens, nextToken, Token.TokenFlag.FlagValid);
                    }

                    continue;
                }

                if (IsSingleCharacterToken(nextToken))
                {
                    AppendTokenTo(token, _tokens[prevToken]);
                    AppendTokenTo(_tokens[nextToken], _tokens[prevToken]);
                    continue;
                }
            }

            // Check for adjacent delimiters
            if (IsUnknownToken(prevToken) && IsDelimiterToken(nextToken))
            {
                char nextDelimiter = _tokens[nextToken].Content[0];
                if (delimiter != nextDelimiter && delimiter != ',')
                {
                    if (nextDelimiter is ' ' or '_')
                    {
                        AppendTokenTo(token, _tokens[prevToken]);
                    }
                }
            }
            else if (IsDelimiterToken(prevToken) && IsDelimiterToken(nextToken))
            {
                char prevDelimiter = _tokens[prevToken].Content[0];
                char nextDelimiter = _tokens[nextToken].Content[0];
                if (prevDelimiter == nextDelimiter && prevDelimiter != delimiter)
                {
                    token.Category = Token.TokenCategory.Unknown; // e.g. "& in "_&_"
                }
            }

            // Check for other special cases
            if (delimiter != '&' && delimiter != '+') continue;
            if (!IsUnknownToken(prevToken) || !IsUnknownToken(nextToken)) continue;
            if (!StringHelper.IsNumericString(_tokens[prevToken].Content)
                || !StringHelper.IsNumericString(_tokens[nextToken].Content)) continue;
            AppendTokenTo(token, _tokens[prevToken]);
            AppendTokenTo(_tokens[nextToken], _tokens[prevToken]); // e.g. 01+02
        }

        // Remove invalid tokens
        _tokens.RemoveAll(token => token.Category == Token.TokenCategory.Invalid);
        return;

        void AppendTokenTo(Token src, Token dest)
        {
            dest.Content += src.Content;
            src.Category = Token.TokenCategory.Invalid;
        }

        bool IsUnknownToken(int it)
        {
            return Token.InListRange(it, _tokens) && _tokens[it].Category == Token.TokenCategory.Unknown;
        }

        bool IsSingleCharacterToken(int it)
        {
            return IsUnknownToken(it) && _tokens[it].Content.Length == 1 && _tokens[it].Content[0] != '-';
        }

        bool IsDelimiterToken(int it)
        {
            return Token.InListRange(it, _tokens) && _tokens[it].Category == Token.TokenCategory.Delimiter;
        }
    }
}
