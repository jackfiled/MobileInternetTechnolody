/*
 * Copyright (c) 2014-2017, Eren Okka
 * Copyright (c) 2016-2017, Paul Miller
 * Copyright (c) 2017-2018, Tyler Bratton
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

namespace AnitomySharp;

/// <summary>
/// A class to manager the list of known anime keywords. This class is analogous to <code>keyword.cpp</code> of Anitomy, and <code>KeywordManager.java</code> of AnitomyJ
/// </summary>
public static class KeywordManager
{
    private static readonly Dictionary<string, Keyword> s_keys = new();
    private static readonly Dictionary<string, Keyword> s_extensions = new();
    private static readonly List<Tuple<ElementCategory, List<string>>> s_peekEntries;

    static KeywordManager()
    {
        var optionsDefault = new KeywordOptions();
        var optionsInvalid = new KeywordOptions(true, true, false);
        var optionsUnidentifiable = new KeywordOptions(false, true, true);
        var optionsUnidentifiableInvalid = new KeywordOptions(false, true, false);
        var optionsUnidentifiableUnsearchable = new KeywordOptions(false, false, true);

        Add(ElementCategory.ElementAnimeSeasonPrefix,
            optionsUnidentifiable,
            new List<string> {"SAISON", "SEASON"});

        Add(ElementCategory.ElementAnimeType,
            optionsUnidentifiable,
            new List<string> {"GEKIJOUBAN", "MOVIE", "OAD", "OAV", "ONA", "OVA", "SPECIAL", "SPECIALS", "TV"});

        Add(ElementCategory.ElementAnimeType,
            optionsUnidentifiableUnsearchable,
            new List<string> {"SP"}); // e.g. "Yumeiro Patissiere SP Professional"

        Add(ElementCategory.ElementAnimeType,
            optionsUnidentifiableInvalid,
            new List<string> {"ED", "ENDING", "NCED", "NCOP", "OP", "OPENING", "PREVIEW", "PV"});

        Add(ElementCategory.ElementAudioTerm,
            optionsDefault,
            new List<string> {
                // Audio channels
                "2.0CH", "2CH", "5.1", "5.1CH", "DTS", "DTS-ES", "DTS5.1",
                "TRUEHD5.1",
                // Audio codec
                "AAC", "AACX2", "AACX3", "AACX4", "AC3", "EAC3", "E-AC-3",
                "FLAC", "FLACX2", "FLACX3", "FLACX4", "LOSSLESS", "MP3", "OGG", "VORBIS",
                // Audio language
                "DUALAUDIO", "DUAL AUDIO"
            });

        Add(ElementCategory.ElementDeviceCompatibility,
            optionsDefault,
            new List<string> {"IPAD3", "IPHONE5", "IPOD", "PS3", "XBOX", "XBOX360"});

        Add(ElementCategory.ElementDeviceCompatibility,
            optionsUnidentifiable,
            new List<string> {"ANDROID"});

        Add(ElementCategory.ElementEpisodePrefix,
            optionsDefault,
            new List<string> {"EP", "EP.", "EPS", "EPS.", "EPISODE", "EPISODE.", "EPISODES", "CAPITULO", "EPISODIO", "FOLGE"});

        Add(ElementCategory.ElementEpisodePrefix,
            optionsInvalid,
            new List<string> {"E", "\\x7B2C"}); // single-letter episode keywords are not valid tokens

        Add(ElementCategory.ElementFileExtension,
            optionsDefault,
            new List<string> {"3GP", "AVI", "DIVX", "FLV", "M2TS", "MKV", "MOV", "MP4", "MPG", "OGM", "RM", "RMVB", "TS", "WEBM", "WMV"});

        Add(ElementCategory.ElementFileExtension,
            optionsInvalid,
            new List<string> {"AAC", "AIFF", "FLAC", "M4A", "MP3", "MKA", "OGG", "WAV", "WMA", "7Z", "RAR", "ZIP", "ASS", "SRT"});

        Add(ElementCategory.ElementLanguage,
            optionsDefault,
            new List<string> {"ENG", "ENGLISH", "ESPANO", "JAP", "PT-BR", "SPANISH", "VOSTFR"});

        Add(ElementCategory.ElementLanguage,
            optionsUnidentifiable,
            new List<string> {"ESP", "ITA"}); // e.g. "Tokyo ESP:, "Bokura ga Ita"

        Add(ElementCategory.ElementOther,
            optionsDefault,
            new List<string> {"REMASTER", "REMASTERED", "UNCENSORED", "UNCUT", "TS", "VFR", "WIDESCREEN", "WS"});

        Add(ElementCategory.ElementReleaseGroup,
            optionsDefault,
            new List<string> {"THORA"});

        Add(ElementCategory.ElementReleaseInformation,
            optionsDefault,
            new List<string> {"BATCH", "COMPLETE", "PATCH", "REMUX"});

        Add(ElementCategory.ElementReleaseInformation,
            optionsUnidentifiable,
            new List<string> {"END", "FINAL"}); // e.g. "The End of Evangelion", 'Final Approach"

        Add(ElementCategory.ElementReleaseVersion,
            optionsDefault,
            new List<string> {"V0", "V1", "V2", "V3", "V4"});

        Add(ElementCategory.ElementSource,
            optionsDefault,
            new List<string> {"BD", "BDRIP", "BLURAY", "BLU-RAY", "DVD", "DVD5", "DVD9", "DVD-R2J", "DVDRIP", "DVD-RIP", "R2DVD", "R2J", "R2JDVD", "R2JDVDRIP", "HDTV", "HDTVRIP", "TVRIP", "TV-RIP", "WEBCAST", "WEBRIP"});

        Add(ElementCategory.ElementSubtitles,
            optionsDefault,
            new List<string> {"ASS", "BIG5", "DUB", "DUBBED", "HARDSUB", "HARDSUBS", "RAW", "SOFTSUB", "SOFTSUBS", "SUB", "SUBBED", "SUBTITLED"});

        Add(ElementCategory.ElementVideoTerm,
            optionsDefault,
            new List<string> {
                // Frame rate
                "23.976FPS", "24FPS", "29.97FPS", "30FPS", "60FPS", "120FPS",
                // Video codec
                "8BIT", "8-BIT", "10BIT", "10BITS", "10-BIT", "10-BITS",
                "HI10", "HI10P", "HI444", "HI444P", "HI444PP",
                "H264", "H265", "H.264", "H.265", "X264", "X265", "X.264",
                "AVC", "HEVC", "HEVC2", "DIVX", "DIVX5", "DIVX6", "XVID",
                // Video format
                "AVI", "RMVB", "WMV", "WMV3", "WMV9",
                // Video quality
                "HQ", "LQ",
                // Video resolution
                "HD", "SD"});

        Add(ElementCategory.ElementVolumePrefix,
            optionsDefault,
            new List<string> {"VOL", "VOL.", "VOLUME"});

        s_peekEntries = new List<Tuple<ElementCategory, List<string>>>
        {
            Tuple.Create(ElementCategory.ElementAudioTerm, new List<string> { "Dual Audio" }),
            Tuple.Create(ElementCategory.ElementVideoTerm, new List<string> { "H264", "H.264", "h264", "h.264" }),
            Tuple.Create(ElementCategory.ElementVideoResolution, new List<string> { "480p", "720p", "1080p" }),
            Tuple.Create(ElementCategory.ElementSource, new List<string> { "Blu-Ray" })
        };
    }

    public static string Normalize(string word)
    {
        return string.IsNullOrEmpty(word) ? word : word.ToUpperInvariant();
    }

    public static bool Contains(ElementCategory category, string keyword)
    {
        var keys = GetKeywordContainer(category);
        if (keys.TryGetValue(keyword, out var foundEntry))
        {
            return foundEntry.Category == category;
        }

        return false;
    }

    /// <summary>
    /// Finds a particular <code>keyword</code>. If found sets <code>category</code> and <code>options</code> to the found search result.
    /// </summary>
    /// <param name="keyword">the keyword to search for</param>
    /// <param name="category">the reference that will be set/changed to the found keyword category</param>
    /// <param name="options">the reference that will be set/changed to the found keyword options</param>
    /// <returns>if the keyword was found</returns>
    public static bool FindAndSet(string keyword, ref ElementCategory category, ref KeywordOptions options)
    {
        Dictionary<string, Keyword> keys = GetKeywordContainer(category);
        if (!keys.TryGetValue(keyword, out var foundEntry))
        {
            return false;
        }

        if (category == ElementCategory.ElementUnknown)
        {
            category = foundEntry.Category;
        }
        else if (foundEntry.Category != category)
        {
            return false;
        }
        options = foundEntry.Options;
        return true;
    }

    /// <summary>
    /// Given a particular <code>filename</code> and <code>range</code> attempt to preidentify the token before we attempt the main parsing logic
    /// </summary>
    /// <param name="filename">the filename</param>
    /// <param name="range">the search range</param>
    /// <param name="elements">elements array that any pre-identified elements will be added to</param>
    /// <param name="preIdentifiedTokens">elements array that any pre-identified token ranges will be added to</param>
    public static void PeekAndAdd(string filename, TokenRange range, List<Element> elements, List<TokenRange> preIdentifiedTokens)
    {
        int endR = range.Offset + range.Size;
        string search = filename.Substring(range.Offset, endR > filename.Length ? filename.Length - range.Offset : endR - range.Offset);
        foreach (var entry in s_peekEntries)
        {
            foreach (string keyword in entry.Item2)
            {
                int foundIdx = search.IndexOf(keyword, StringComparison.CurrentCulture);
                if (foundIdx == -1) continue;
                foundIdx += range.Offset;
                elements.Add(new Element(entry.Item1, keyword));
                preIdentifiedTokens.Add(new TokenRange(foundIdx, keyword.Length));
            }
        }
    }

    // Private API

    /** Returns the appropriate keyword container. */
    private static Dictionary<string, Keyword> GetKeywordContainer(ElementCategory category)
    {
        return category == ElementCategory.ElementFileExtension ? s_extensions : s_keys;
    }

    /// Adds a <code>category</code>, <code>options</code>, and <code>keywords</code> to the internal keywords list.
    private static void Add(ElementCategory category, KeywordOptions options, IEnumerable<string> keywords)
    {
        var keys = GetKeywordContainer(category);
        foreach (string key in keywords.Where(k => !string.IsNullOrEmpty(k) && !keys.ContainsKey(k)))
        {
            keys[key] = new Keyword(category, options);
        }
    }
}

/// <summary>
/// Keyword options for a particular keyword.
/// </summary>
public class KeywordOptions
{
    public bool Identifiable { get; }
    public bool Searchable { get; }
    public bool Valid { get; }

    public KeywordOptions() : this(true, true, true) {}

    /// <summary>
    /// Constructs a new keyword options
    /// </summary>
    /// <param name="identifiable">if the token is identifiable</param>
    /// <param name="searchable">if the token is searchable</param>
    /// <param name="valid">if the token is valid</param>
    public KeywordOptions(bool identifiable, bool searchable, bool valid)
    {
        Identifiable = identifiable;
        Searchable = searchable;
        Valid = valid;
    }

}

/// <summary>
/// A Keyword
/// </summary>
public struct Keyword
{
    public readonly ElementCategory Category;
    public readonly KeywordOptions Options;

    /// <summary>
    /// Constructs a new Keyword
    /// </summary>
    /// <param name="category">the category of the keyword</param>
    /// <param name="options">the keyword's options</param>
    public Keyword(ElementCategory category, KeywordOptions options)
    {
        Category = category;
        Options = options;
    }
}
