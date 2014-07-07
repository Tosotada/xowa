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
package gplx.xowa.apis.xowa.gui.browsers; import gplx.*; import gplx.xowa.*; import gplx.xowa.apis.*; import gplx.xowa.apis.xowa.*; import gplx.xowa.apis.xowa.gui.*;
import gplx.gfui.*; import gplx.xowa.gui.*; import gplx.xowa.gui.views.*;
public class Xoapi_html_box implements GfoInvkAble {
	private Xog_win_itm win;
	public void Init_by_kit(Xoa_app app) {this.win = app.Gui_mgr().Browser_win();}
	public void Focus() {
		Xog_tab_itm tab = win.Active_tab(); if (tab == Xog_tab_itm_.Null) return;
		Gfui_html html_box = tab.Html_itm().Html_box();
		html_box.Focus();
		if (tab.View_mode() != Xog_page_mode.Tid_read)	// if edit / html, place focus in edit box
			html_box.Html_elem_focus(Xog_html_itm.Elem_id__xowa_edit_data_box);
	}
	public void Selection_focus() {
		Xog_tab_itm tab = win.Active_tab(); if (tab == Xog_tab_itm_.Null) return;
		Gfui_html html_box = tab.Html_itm().Html_box();
		html_box.Html_doc_selection_focus_toggle();
	}
	public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, Invk_focus)) 					this.Focus();
		else if	(ctx.Match(k, Invk_selection_focus_toggle)) 		this.Selection_focus();
		else	return GfoInvkAble_.Rv_unhandled;
		return this;
	}
	private static final String Invk_focus = "focus", Invk_selection_focus_toggle = "selection_focus_toggle";
}
