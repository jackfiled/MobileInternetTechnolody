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
/// AnitomySharp search configuration options
/// </summary>
public class Options(
    string delimiters = " _.&+,|",
    bool episode = true,
    bool title = true,
    bool extension = true,
    bool group = true)
{
    public string AllowedDelimiters { get; } = delimiters;
    public bool ParseEpisodeNumber { get; } = episode;
    public bool ParseEpisodeTitle { get; } = title;
    public bool ParseFileExtension { get; } = extension;
    public bool ParseReleaseGroup { get; } = group;
}
