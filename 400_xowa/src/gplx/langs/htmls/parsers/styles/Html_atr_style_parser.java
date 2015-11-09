/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012 gnosygnu@gmail.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package gplx.langs.htmls.parsers.styles; import gplx.*; import gplx.langs.*; import gplx.langs.htmls.*; import gplx.langs.htmls.parsers.*;
public class Html_atr_style_parser {
	public static void Parse(byte[] src, int src_bgn, int src_end, Html_atr_style_wkr wkr) {
		int atr_idx = 0, atr_bgn = -1, atr_end = -1, key_bgn = -1, key_end = -1, tmp_bgn = -1, tmp_end = -1;
		int pos = src_bgn;
		while (true) {
			boolean pos_is_last = pos == src_end;
			byte b = pos_is_last ? Byte_ascii.Semic : src[pos];
			switch (b) {
				case Byte_ascii.Semic:
					if (key_bgn != -1) { // ignore empty atrs
						if (!wkr.On_atr(src, atr_idx, atr_bgn, atr_end, key_bgn, key_end, tmp_bgn, tmp_end))
							pos_is_last = true;
					}
					++atr_idx; atr_bgn = -1; atr_end = -1; key_bgn = -1; key_end = -1; tmp_bgn = -1; tmp_end = -1;
					break;
				case Byte_ascii.Colon:
					key_bgn = tmp_bgn;
					key_end = tmp_end;
					tmp_bgn = -1; tmp_end = -1;
					break;
				case Byte_ascii.Tab: case Byte_ascii.Nl: case Byte_ascii.Cr: case Byte_ascii.Space:						
					break;
				default:
					if (tmp_bgn == -1) tmp_bgn = pos;
					tmp_end = pos + 1;
					break;
			}
			if (pos_is_last) break;
			++pos;
		}
	}
}