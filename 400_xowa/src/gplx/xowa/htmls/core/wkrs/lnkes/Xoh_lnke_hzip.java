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
package gplx.xowa.htmls.core.wkrs.lnkes; import gplx.*; import gplx.xowa.*; import gplx.xowa.htmls.*; import gplx.xowa.htmls.core.*; import gplx.xowa.htmls.core.wkrs.*;
import gplx.core.brys.*; import gplx.core.threads.poolables.*; import gplx.xowa.wikis.ttls.*;
import gplx.langs.htmls.*; import gplx.xowa.htmls.core.hzips.*;
public class Xoh_lnke_hzip implements Xoh_hzip_wkr, Gfo_poolable_itm {
	public String Key() {return Xoh_hzip_dict_.Key__lnke;}
	public Xoh_lnke_hzip Encode(Bry_bfr bfr, Hzip_stat_itm stat_itm, byte[] src, Xoh_lnke_parser arg) {
		byte anch_cls_type = arg.Anch_cls_type();
		boolean auto_exists = arg.Auto_id() != -1;
		boolean text_exists = arg.Capt_end() != -1;
		flag_bldr.Set(Flag__auto_exists		, auto_exists);
		flag_bldr.Set(Flag__text_exists		, text_exists);
		flag_bldr.Set(Flag__anch_cls		, anch_cls_type);

		switch (anch_cls_type) {
			case Xoh_lnke_dict_.Type__free:		stat_itm.Lnke__free__add();break;
			case Xoh_lnke_dict_.Type__auto:		stat_itm.Lnke__auto__add(); break;
			case Xoh_lnke_dict_.Type__text:		stat_itm.Lnke__text__add(); break;
		}

		bfr.Add(Xoh_hzip_dict_.Bry__lnke);								// add hook
		Xoh_hzip_int_.Encode(1, bfr, flag_bldr.Encode());				// add flag
		bfr.Add_mid(src, arg.Href_bgn(), arg.Href_end());				// add href
		bfr.Add_byte(Xoh_hzip_dict_.Escape);
		if		(auto_exists)
			Xoh_hzip_int_.Encode(1, bfr, arg.Auto_id());
		else if (text_exists) {
			bfr.Add_mid(src, arg.Capt_bgn(), arg.Capt_end());			// add capt
			bfr.Add_byte(Xoh_hzip_dict_.Escape);
		}			
		return this;
	}
	public int Decode(Bry_bfr bfr, boolean write_to_bfr, Xoh_hdoc_ctx ctx, Xoh_page hpg, Bry_rdr rdr, byte[] src, int hook_bgn) {
		int flag = rdr.Read_int_by_base85(1);
		flag_bldr.Decode(flag);
		boolean auto_exists		= flag_bldr.Get_as_bool(Flag__auto_exists);
		boolean text_exists		= flag_bldr.Get_as_bool(Flag__text_exists);
		byte anch_cls_type		= flag_bldr.Get_as_byte(Flag__anch_cls);

		int href_bgn = rdr.Pos();
		int href_end = rdr.Find_fwd_lr();
		int auto_id = -1, capt_bgn = -1, capt_end = -1;
		if		(auto_exists)
			auto_id = rdr.Read_int_by_base85(1);
		else if (text_exists) {
			capt_bgn = rdr.Pos();
			capt_end = rdr.Find_fwd_lr();
		}
		int rv = rdr.Pos();

		bfr.Add(Html_bldr_.Bry__a_lhs_w_href);
		bfr.Add_mid(src, href_bgn, href_end);
		bfr.Add(Xoh_lnke_dict_.Html__atr__0).Add(Xoh_lnke_dict_.To_html_class(anch_cls_type)).Add(Xoh_lnke_dict_.Html__rhs_end);
		if		(auto_exists)
			bfr.Add_byte(Byte_ascii.Brack_bgn).Add_int_variable(auto_id).Add_byte(Byte_ascii.Brack_end);
		else if (text_exists)
			bfr.Add_mid(src, capt_bgn, capt_end);
		else
			bfr.Add_mid(src, href_bgn, href_end);
		bfr.Add(Html_bldr_.Bry__a_rhs);

		return rv;
	}
	public int				Pool__idx() {return pool_idx;} private int pool_idx;
	public void				Pool__clear (Object[] args) {}
	public void				Pool__rls	() {pool_mgr.Rls_fast(pool_idx);} private Gfo_poolable_mgr pool_mgr;
	public Gfo_poolable_itm	Pool__make	(Gfo_poolable_mgr mgr, int idx, Object[] args) {Xoh_lnke_hzip rv = new Xoh_lnke_hzip(); rv.pool_mgr = mgr; rv.pool_idx = idx; return rv;}
	private final Int_flag_bldr flag_bldr = new Int_flag_bldr().Pow_ary_bld_ (1, 1, 2);
	private static final int // SERIALIZED
	  Flag__auto_exists		=  0
	, Flag__text_exists		=  1
	, Flag__anch_cls		=  2	// "free", "autonumber", "text"
	;
}
