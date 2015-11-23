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
package gplx.xowa.htmls.core.wkrs.thms; import gplx.*; import gplx.xowa.*; import gplx.xowa.htmls.*; import gplx.xowa.htmls.core.*; import gplx.xowa.htmls.core.wkrs.*;
import org.junit.*; import gplx.xowa.htmls.core.makes.tests.*;
public class Xoh_thm_html_tst {
	private final Xoh_make_fxt fxt = new Xoh_make_fxt();
	@Test   public void Thumb__basic() {
		// fxt.Expd_itms_xfers(fxt.Make_xfer("A.png", 0, 0, 0, Bool_.Y, Xof_ext_.Id_png));
		fxt.Test__html("[[File:A.png|thumb|test_caption]]", String_.Concat_lines_nl_skip_last
		( "<div class='thumb tright'>"
		, "  <div id='xowa_file_div_0' class='thumbinner' style='width:220px;'>"
		, "    <a href='/wiki/File:A.png' class='image' xowa_title='A.png'><img data-xoimg='8|-1|-1|-1|-1|-1' src='' width='0' height='0' alt=''/></a>"
		, "    <div class='thumbcaption'>"
		, "      <div class='magnify'>"
		, "        <a href='/wiki/File:A.png' class='internal' title='Enlarge'>"
		, "          <img src='file:///mem/xowa/bin/any/xowa/file/mediawiki.file/magnify-clip.png' width='15' height='11' alt='' />"
		, "        </a>"
		, "      </div>"
		, "      test_caption"
		, "    </div>"
		, "  </div>"
		, "</div>"
		));
	}
}
