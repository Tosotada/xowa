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
package gplx.xowa.parsers.vnts; import gplx.*; import gplx.xowa.*; import gplx.xowa.parsers.*;
import gplx.xowa.langs.vnts.*;
class Vnt_flag_parser implements gplx.core.brys.Bry_split_wkr {
	private final Hash_adp_bry codes_regy = Vnt_flag_code_.Regy;
	private Vnt_flag_code_mgr codes; private Vnt_flag_lang_mgr langs;
	private Xol_vnt_regy vnt_regy;
	public void Parse(Vnt_flag_code_mgr codes, Vnt_flag_lang_mgr langs, Xol_vnt_regy vnt_regy, byte[] src, int src_bgn, int src_end) {
		this.codes = codes; this.langs = langs; this.vnt_regy = vnt_regy;
		codes.Clear(); langs.Clear();
		if (src_end != Bry_find_.Not_found)			// "|" found; EX: -{A|}-
			Bry_split_.Split(src, src_bgn, src_end, Byte_ascii.Semic, true, this);
		int codes_count = codes.Count(), langs_count = langs.Count();
		if		(codes_count == 0)											codes.Set_y(Vnt_flag_code_.Tid_show);
		else if (codes.Limit_if_exists(Vnt_flag_code_.Tid_raw))				{}
		else if (codes.Limit_if_exists(Vnt_flag_code_.Tid_name))			{}
		else if (codes.Limit_if_exists(Vnt_flag_code_.Tid_del))				{}
		else if (codes_count == 1 && codes.Get(Vnt_flag_code_.Tid_title))	codes.Set_y(Vnt_flag_code_.Tid_hide);
		else if (codes.Get(Vnt_flag_code_.Tid_hide)) {
			boolean exists_d = codes.Get(Vnt_flag_code_.Tid_descrip);
			boolean exists_t = codes.Get(Vnt_flag_code_.Tid_title);
			codes.Clear();
			codes.Set_y_many(Vnt_flag_code_.Tid_add, Vnt_flag_code_.Tid_hide);
			if (exists_d) codes.Set_y(Vnt_flag_code_.Tid_descrip);
			if (exists_t) codes.Set_y(Vnt_flag_code_.Tid_title);
		}
		else {
			if (codes.Get(Vnt_flag_code_.Tid_aout))
				codes.Set_y_many(Vnt_flag_code_.Tid_add, Vnt_flag_code_.Tid_show);
			if (codes.Get(Vnt_flag_code_.Tid_descrip))
				codes.Set_n(Vnt_flag_code_.Tid_show);
			if (langs_count > 0)
				codes.Clear();
		}
	}
	public int Split(byte[] src, int itm_bgn, int itm_end) {
		int flag_tid = codes_regy.Get_as_int_or(src, itm_bgn, itm_end, -1);
		if (flag_tid == -1) {	// try to find flags like "zh-hans", "zh-hant"; allow syntaxes like "-{zh-hans;zh-hant|XXXX}-"
			Xol_vnt_itm vnt_itm = vnt_regy.Get_by(src, itm_bgn, itm_end);
			if (vnt_itm == null) return Bry_split_.Rv__ok; // unknown flag; ignore
			langs.Add(vnt_itm);
			return Bry_split_.Rv__ok;
		}
		codes.Add(flag_tid);
		return Bry_split_.Rv__ok;
	}
}
