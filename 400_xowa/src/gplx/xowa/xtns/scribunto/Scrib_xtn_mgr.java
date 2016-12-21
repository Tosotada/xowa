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
package gplx.xowa.xtns.scribunto; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*;
import gplx.xowa.parsers.logs.*;
import gplx.xowa.xtns.scribunto.engines.*;
public class Scrib_xtn_mgr extends Xox_mgr_base {
	@Override public byte[] Xtn_key() {return XTN_KEY;} public static final    byte[] XTN_KEY = Bry_.new_a7("scribunto");
	@Override public void Xtn_ctor_by_app(Xoae_app app) {this.app = app;} private Xoae_app app;
	@Override public Xox_mgr Xtn_clone_new() {return new Scrib_xtn_mgr();}
	public Scrib_lib_mgr Lib_mgr() {return lib_mgr;} private Scrib_lib_mgr lib_mgr = new Scrib_lib_mgr();
	@Override public void Enabled_(boolean v) {
		Scrib_core_mgr.Term_all(app);
		super.Enabled_(v);
	}
	public byte Engine_type() {return engine_type;} private byte engine_type = Scrib_engine_type.Type_luaj;
	public void Engine_type_(byte cmd) {
		engine_type = cmd;
		if (app != null) gplx.xowa.xtns.scribunto.Scrib_core_mgr.Term_all(app);
	}
	public int Lua_timeout() {return lua_timeout;} private int lua_timeout = 4000;
	public int Lua_timeout_polling() {return lua_timeout_polling;} private int lua_timeout_polling = 1;
	public int Lua_timeout_busy_wait() {return lua_timeout_busy_wait;} private int lua_timeout_busy_wait = 250;
	public int Lua_timeout_loop() {return lua_timeout_loop;} private int lua_timeout_loop = 10000000;
	public boolean Lua_log_enabled() {return lua_log_enabled;} private boolean lua_log_enabled = false;
	public boolean Luaj_debug_enabled() {return luaj_debug_enabled;} private boolean luaj_debug_enabled;
	public void Luaj_debug_enabled_(boolean v) {
		this.luaj_debug_enabled = v;
		gplx.xowa.xtns.scribunto.Scrib_core_mgr.Term_all(app);// restart server in case luaj caches any debug data
	}
	public Xop_log_invoke_wkr Invoke_wkr() {return invoke_wkr;} private Xop_log_invoke_wkr invoke_wkr;
	@Override public void Xtn_init_by_wiki(Xowe_wiki wiki) {
		wiki.App().Cfg().Bind_many_wiki(this, wiki, Cfg__enabled, Cfg__engine, Cfg__lua__timeout, Cfg__lua__timeout_busy_wait, Cfg__lua__timeout_polling);
	}

	@Override public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, Invk_invoke_wkr))						return m.ReadYnOrY("v") ? Invoke_wkr_or_new() : Gfo_invk_.Noop;
		else if	(ctx.Match(k, Cfg__enabled))						Enabled_(m.ReadYn("v"));
		else if	(ctx.Match(k, Cfg__engine))							Engine_type_(Scrib_engine_type.Xto_byte(m.ReadStr("v")));
		else if	(ctx.Match(k, Cfg__lua__timeout))					lua_timeout = m.ReadInt("v");
		else if	(ctx.Match(k, Cfg__lua__timeout_polling))			lua_timeout_polling = m.ReadInt("v");
		else if	(ctx.Match(k, Cfg__lua__timeout_busy_wait))			lua_timeout_busy_wait = m.ReadInt("v");
		else														return super.Invk(ctx, ikey, k, m);
		return this;
	}
	private static final String Invk_invoke_wkr = "invoke_wkr";
	public Xop_log_invoke_wkr Invoke_wkr_or_new() {
		if (invoke_wkr == null) invoke_wkr = app.Log_mgr().Make_wkr_invoke();
		return invoke_wkr;
	}
	public static Err err_(String fmt, Object... args)						{return Err_.new_wo_type(fmt, args).Trace_ignore_add_1_();}
	public static Err err_(Exception e, String msg, Object... args)	{return Err_.new_exc(e, "xo", msg, args).Trace_ignore_add_1_();}
	private static final String 
	  Cfg__enabled					= "xowa.addon.scribunto.enabled"
	, Cfg__engine					= "xowa.addon.scribunto.engine"
	, Cfg__lua__timeout				= "xowa.addon.scribunto.lua.timeout"
	, Cfg__lua__timeout_polling		= "xowa.addon.scribunto.lua.timeout_polling"
	, Cfg__lua__timeout_busy_wait	= "xowa.addon.scribunto.lua.timeout_busy_wait"
	;

}
