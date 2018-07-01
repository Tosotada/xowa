/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012-2017 gnosygnu@gmail.com

XOWA is licensed under the terms of the General Public License (GPL) Version 3,
or alternatively under the terms of the Apache License Version 2.0.

You may use XOWA according to either of these licenses as is most appropriate
for your project on a case-by-case basis.

The terms of each license can be found in the source code repository:

GPLv3 License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-GPLv3.txt
Apache License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-APACHE2.txt
*/
package gplx.xowa.xtns.wbases.stores; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*; import gplx.xowa.xtns.wbases.*;
import gplx.langs.jsons.*;
import gplx.xowa.wikis.pages.*;
import gplx.xowa.apps.caches.*;
import gplx.xowa.xtns.wbases.core.*;
public class Wbase_doc_mgr {
	private final    Wdata_wiki_mgr wbase_mgr;
	private final    Wbase_qid_mgr qid_mgr;
	private final    Wbase_doc_cache doc_cache;
	public Wbase_doc_mgr(Wdata_wiki_mgr wbase_mgr, Wbase_qid_mgr qid_mgr, Wbase_doc_cache doc_cache) {
		this.wbase_mgr = wbase_mgr;
		this.qid_mgr = qid_mgr;
		this.doc_cache = doc_cache;
	}
	public void Enabled_(boolean v) {this.enabled = v;} private boolean enabled;
	public void Clear() {
		synchronized (doc_cache) {	// LOCK:app-level
			doc_cache.Clear();
		}
	}
	public Wdata_doc Get_by_ttl_or_null(Xowe_wiki wiki, Xoa_ttl ttl) { // "enwiki", "Earth" -> "Q2" wdoc
		byte[] qid_bry = qid_mgr.Get_qid_or_null(wiki, ttl);	// EX: "enwiki", "Earth" -> "Q2"
		return qid_bry == null ? null : this.Get_by_exact_id_or_null(qid_bry);
	}
	public Wdata_doc Get_by_xid_or_null(byte[] xid) {return Get_by_loose_id_or_null(Wbase_pid.Prepend_property_if_needed(xid));}// scribunto passes either p1 or q1; convert p1 to "Property:p1"
	public Wdata_doc Get_by_loose_id_or_null(byte[] ttl_bry) {
		return Get_by_exact_id_or_null(ttl_bry);
	}
	public Wdata_doc Get_by_exact_id_or_null(byte[] ttl_bry) {// must correct case and ns; EX:"Q2" or "Property:P1"; not "q2" or "P2"
		// load from cache
		Wdata_doc rv = doc_cache.Get_or_null(ttl_bry);
		if (rv == null) {
			// load from db
			rv = Load_wdoc_or_null(ttl_bry); 
			if (rv == null) return null;	// page not found
			Add(ttl_bry, rv);// NOTE: use ttl_bry, not rv.Qid; allows subsequent lookups to skip this redirect cycle
		}
		return rv;
	}
	private Wdata_doc Load_wdoc_or_null(byte[] ttl_bry) { // EX:"Q2" or "Property:P1"
		if (!enabled) return null;
		synchronized (doc_cache) {	// LOCK:app-level; jdoc_parser; moved synchronized higher up; DATE:2016-09-03
			byte[] cur_ttl_bry = ttl_bry;
			int load_count = -1;
			while (load_count < 2) {	// limit to 2 tries (i.e.: 1 redirect)
				// parse ttl; note that "q2" will get parsed to "Q2" b/c of ns casing
				Xoa_ttl cur_ttl = wbase_mgr.Wdata_wiki().Ttl_parse(cur_ttl_bry);
				if (cur_ttl == null) {
					Gfo_usr_dlg_.Instance.Warn_many("", "", "invalid wbase ttl: orig=~{0} cur=~{1}", ttl_bry, cur_ttl_bry);
					return null;
				}

				// get page
				Xoae_page page = wbase_mgr.Wdata_wiki().Data_mgr().Load_page_by_ttl(cur_ttl);
				if (!page.Db().Page().Exists()) return null;

				// parse jdoc
				Json_doc jdoc = wbase_mgr.Jdoc_parser().Parse(page.Db().Text().Text_bry());
				if (jdoc == null) {
					Gfo_usr_dlg_.Instance.Warn_many("", "", "invalid jdoc for ttl: orig=~{0} cur=~{1}", ttl_bry, cur_ttl_bry);
					return null;
				}

				// check for redirect; EX: {"entity":"Q22350516","redirect":"Q21006972"}; PAGE:fr.w:Tour_du_Táchira_2016; DATE:2016-08-13
				Json_nde jdoc_root = jdoc.Root_nde();
				byte[] redirect_ttl = jdoc_root.Get_as_bry_or(Bry__redirect, null);
				if (redirect_ttl != null) {
					cur_ttl_bry = redirect_ttl;
					continue;
				}

				// is json doc, and not a redirect; return
				return new Wdata_doc(cur_ttl_bry, wbase_mgr, jdoc);
			}
			Gfo_usr_dlg_.Instance.Warn_many("", "", "too many redirects for ttl: orig=~{0} cur=~{1}", ttl_bry, cur_ttl_bry);
		}
		return null;
	}
	private static final    byte[] Bry__redirect = Bry_.new_a7("redirect");

	public void Add(byte[] full_db, Wdata_doc page) {	// TEST:
		synchronized (doc_cache) {	// LOCK:app-level
			if (doc_cache.Get_or_null(full_db) == null)
				doc_cache.Add(full_db, page);
		}
	}	
}
