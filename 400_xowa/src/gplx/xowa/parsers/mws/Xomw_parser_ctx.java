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
package gplx.xowa.parsers.mws; import gplx.*; import gplx.xowa.*; import gplx.xowa.parsers.*;
public class Xomw_parser_ctx {
	public Xomw_parser_ctx(Xoa_ttl page_ttl) {
		this.page_ttl = page_ttl;
	}
	public Xoa_ttl Page_ttl() {return page_ttl;} private Xoa_ttl page_ttl;
	public static final int Pos__bos = -1;
}