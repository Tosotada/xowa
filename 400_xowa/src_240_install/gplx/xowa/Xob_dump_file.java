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
package gplx.xowa; import gplx.*;
import gplx.ios.*;
import gplx.xowa.wikis.*;
public class Xob_dump_file {
	public Xow_domain Wiki_type()		{return wiki_domain_itm;} private Xow_domain wiki_domain_itm;
	public String Dump_date()			{return dump_date;} public void Dump_date_(String v) {dump_date = v;}  String dump_date;
	public String Dump_file_type()		{return dump_file_type;} private String dump_file_type;
	public String Server_url()			{return server_url;} private String server_url;
	public String File_url()			{return file_url;} private String file_url;
	public String File_name()			{return file_name;} private String file_name;
	public long File_len()				{return file_len;} long file_len;
	public DateAdp File_modified()		{return file_modified;} DateAdp file_modified;
	public byte[] Wiki_alias()			{return wiki_alias;} private byte[] wiki_alias;
	public Xob_dump_file Ctor(String wiki_domain, String dump_date, String dump_file_type) {
		this.dump_date = dump_date; this.dump_file_type = dump_file_type;
		this.wiki_domain_itm = Xow_domain_.parse(Bry_.new_a7(wiki_domain));
		this.wiki_alias = Xow_wiki_alias.Build_alias(wiki_domain_itm);
		byte[] dump_file_bry = Bry_.new_u8(dump_file_type);
		byte dump_file_tid = Xow_wiki_alias.Parse__tid(dump_file_bry);
		byte[] ext = Xob_dump_file_.Ext_xml_bz2;
		switch (dump_file_tid) {
			case Xow_wiki_alias.Tid_page_props: case Xow_wiki_alias.Tid_categorylinks: case Xow_wiki_alias.Tid_image:
				ext = Xob_dump_file_.Ext_sql_gz;
				break;
		}
		this.file_name = String_.new_u8(Xob_dump_file_.Bld_dump_file_name(wiki_alias, Bry_.new_u8(dump_date), dump_file_bry, ext));
		return this;
	}
	public void Server_url_(String server_url) {
		this.server_url = server_url;
		String dump_dir_url = String_.new_u8(Xob_dump_file_.Bld_dump_dir_url(Bry_.new_u8(server_url), wiki_alias, Bry_.new_u8(dump_date)));
		this.file_url = dump_dir_url + file_name;
	}
	public boolean Connect() {
		IoEngine_xrg_downloadFil args = Io_mgr.I.DownloadFil_args("", Io_url_.Empty);
		boolean rv = Connect_exec(args, file_url);
		// WMF changed dumping approach to partial dumps; this sometimes causes /latest/ to be missing page_articles; try to get earlier dump; DATE:2015-07-09
		if (	!rv	// not found
			&&	String_.In(server_url, Xob_dump_file_.Server_wmf_http, Xob_dump_file_.Server_wmf_https)	// server is dumps.wikimedia.org
			&&	String_.Eq(dump_date, Xob_dump_file_.Date_latest)	// request dump was latest
			) {
			Xoa_app_.Usr_dlg().Warn_many("", "", "wmf.dump:latest not found; url=~{0}", file_url);
			byte[] abrv_wm = Xow_wiki_alias.Build_alias(wiki_domain_itm);
			String new_dump_root = Xob_dump_file_.Server_wmf_https + String_.new_u8(abrv_wm) + "/";	// EX: http://dumps.wikimedia.org/enwiki/
			byte[] wiki_dump_dirs_src = args.Exec_as_bry(new_dump_root);
			if (wiki_dump_dirs_src == null) {Xoa_app_.Usr_dlg().Warn_many("", "", "could not connect to dump server; url=~{0}", new_dump_root); return false;}
			String[] dates = gplx.xowa.wmfs.dump_pages.Xowmf_wiki_dump_dirs_parser.Parse(wiki_domain_itm.Domain_bry(), wiki_dump_dirs_src);
			int dates_len = dates.length;
			for (int i = dates_len - 1; i > -1; --i) {
				String new_dump_date = dates[i];
				if (String_.Eq(new_dump_date, Xob_dump_file_.Date_latest)) continue;	// skip latest; assume it is bad
				String new_dump_file = String_.Replace(file_name, Xob_dump_file_.Date_latest, new_dump_date);	// replace "-latest-" with "-20150602-";
				String new_file_url = new_dump_root + new_dump_date + "/" + new_dump_file;
				rv = Connect_exec(args, new_file_url);
				if (rv) {
					Xoa_app_.Usr_dlg().Note_many("", "", "wmf.dump:dump found; url=~{0}", new_file_url);
					dump_date = new_dump_date;
					file_name = new_dump_file;
					file_url = new_file_url;
					break;
				}
				else
					Xoa_app_.Usr_dlg().Warn_many("", "", "wmf.dump:dump not found; url=~{0}", new_file_url);
			}
		}
		return rv;
	}
	private boolean Connect_exec(IoEngine_xrg_downloadFil args, String cur_file_url) {			
		boolean rv = args.Src_last_modified_query_(true).Exec_meta(cur_file_url);
		long tmp_file_len = args.Src_content_length();
		DateAdp tmp_file_modified = args.Src_last_modified();
		Xoa_app_.Usr_dlg().Note_many("", "", "wmf.dump:connect rslts; url=~{0} result=~{1} fil_len=~{2} file_modified=~{3} server_url=~{4} dump_date=~{5}", cur_file_url, rv, tmp_file_len, tmp_file_modified == null ? "<<NULL>>" : tmp_file_modified.XtoStr_fmt_yyyy_MM_dd_HH_mm_ss(), server_url, dump_date);
		if (rv) {
			if (tmp_file_modified != null && tmp_file_modified.Year() <= 1970) return false;	// url has invalid file; note that dumps.wikimedia.org currently returns back an HTML page with "404 not found"; rather than try to download and parse this (since content may change), use the date_modified which always appears to be UnixTime 0; DATE:2015-07-21
			file_len = tmp_file_len;
			file_modified = tmp_file_modified;
		}
		return rv;
	}
	public static Xob_dump_file new_(String wiki_domain, String dump_date, String dump_type) {return new Xob_dump_file().Ctor(wiki_domain, dump_date, dump_type);}
}