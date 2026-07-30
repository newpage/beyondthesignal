package com.beyondsignal.game.debug;

import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.runtime.SimulationRuntime;
import com.beyondsignal.game.websocket.SessionEventHub;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.Objects;
import java.util.UUID;

public final class BridgeDebugRoutes {
    private final GameSessionService sessionService;
    private final SimulationRuntime simulationRuntime;
    private final SessionEventHub eventHub;
    private final BridgeDebugSnapshotFactory snapshotFactory = new BridgeDebugSnapshotFactory();

    public BridgeDebugRoutes(
        GameSessionService sessionService,
        SimulationRuntime simulationRuntime,
        SessionEventHub eventHub
    ) {
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService must not be null");
        this.simulationRuntime = Objects.requireNonNull(simulationRuntime, "simulationRuntime must not be null");
        this.eventHub = Objects.requireNonNull(eventHub, "eventHub must not be null");
    }

    public void mount(Router router) {
        router.get("/bridge").handler(ctx -> bridgePage(ctx, "ALL"));
        router.get("/bridge/captain").handler(ctx -> bridgePage(ctx, "CAPTAIN"));
        router.get("/bridge/helm").handler(ctx -> bridgePage(ctx, "HELM"));
        router.get("/bridge/tactical").handler(ctx -> bridgePage(ctx, "TACTICAL"));
        router.get("/bridge/engineering").handler(ctx -> bridgePage(ctx, "ENGINEERING"));
        router.get("/bridge/science").handler(ctx -> bridgePage(ctx, "SCIENCE"));
        router.get("/bridge/communications").handler(ctx -> bridgePage(ctx, "COMMUNICATIONS"));

        router.get("/debug/bridge").handler(ctx -> ctx.response()
            .putHeader("Content-Type", "text/html; charset=utf-8")
            .end(PAGE));

        router.get("/api/debug/bridge/sessions").handler(ctx -> {
            JsonArray sessions = new JsonArray();
            sessionService.listSessions().stream()
                .filter(session -> simulationRuntime.findState(session.id()).isPresent())
                .map(session -> new JsonObject()
                    .put("id", session.id().toString())
                    .put("sessionName", session.sessionName())
                    .put("shipName", session.shipName())
                    .put("status", session.status().name()))
                .forEach(sessions::add);
            ctx.json(new JsonObject().put("sessions", sessions));
        });

        router.get("/api/debug/bridge/sessions/:sessionId").handler(ctx -> {
            try {
                UUID sessionId = UUID.fromString(ctx.pathParam("sessionId"));
                var diagnostics = simulationRuntime.findDiagnostics(sessionId);
                if (diagnostics.isEmpty()) {
                    ctx.response().setStatusCode(404).end(new JsonObject()
                        .put("message", "Active simulation not found")
                        .encode());
                    return;
                }
                ctx.json(snapshotFactory.create(
                    sessionService.getSession(sessionId),
                    diagnostics.orElseThrow(),
                    eventHub.subscriberCount(sessionId)
                ));
            } catch (IllegalArgumentException exception) {
                ctx.response().setStatusCode(400).end(new JsonObject()
                    .put("message", "Invalid sessionId")
                    .encode());
            } catch (RuntimeException exception) {
                ctx.response().setStatusCode(404).end(new JsonObject()
                    .put("message", exception.getMessage())
                    .encode());
            }
        });
    }


    private static void bridgePage(io.vertx.ext.web.RoutingContext ctx, String station) {
        ctx.response().putHeader("Content-Type", "text/html; charset=utf-8")
            .end(BRIDGE_PAGE.replace("__STATION__", station));
    }

    private static final String BRIDGE_PAGE = """
        <!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
        <title>Beyond the Signal Bridge — __STATION__</title><style>body{margin:0;background:#050b12;color:#d9f3ff;font:14px system-ui}header,main{max-width:1200px;margin:auto;padding:18px}.grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px}.p{background:#0c1b28;border:1px solid #23445a;border-radius:10px;padding:16px}input,select,button{width:100%;padding:10px;margin:5px 0;background:#102636;color:#fff;border:1px solid #31566d;border-radius:6px}button{cursor:pointer}pre{white-space:pre-wrap;color:#86e1ff}.alert{color:#ff6b6b}@media(max-width:800px){.grid{grid-template-columns:1fr}}</style></head>
        <body data-station="__STATION__"><header><h1>Beyond the Signal — __STATION__ Station</h1><input id="session" placeholder="Session UUID"><input id="player" placeholder="Player UUID"><button onclick="connect()">Connect</button><span id="status"></span></header>
        <main class="grid"><section class="p"><h2>Helm</h2><input id="throttle" type="number" min="0" max="100" value="50"><button onclick="send('SET_THROTTLE',{throttle:+throttle.value})">Set Throttle</button><input id="heading" type="number" min="0" max="359" value="90"><button onclick="send('SET_HEADING',{headingDegrees:+heading.value})">Set Heading</button></section>
        <section class="p"><h2>Tactical</h2><button onclick="send('SET_SHIELDS',{raised:true})">Raise Shields</button><button onclick="send('SET_SHIELDS',{raised:false})">Lower Shields</button><input id="target" placeholder="Target UUID"><button onclick="send('SELECT_TARGET',{targetId:target.value})">Select Target</button><button onclick="send('FIRE_WEAPON',{weapon:'PHASER'})">Fire Phaser</button><button onclick="send('FIRE_WEAPON',{weapon:'TORPEDO'})">Fire Torpedo</button></section>
        <section class="p"><h2>Engineering</h2><select id="system"><option>ENGINES</option><option>SHIELDS</option><option>SENSORS</option><option>WEAPONS</option><option>LIFE_SUPPORT</option></select><input id="power" type="number" min="0" max="100" value="20"><button onclick="send('ALLOCATE_POWER',{subsystem:system.value,powerAllocation:+power.value})">Allocate Power</button></section>
        <section class="p"><h2>Science</h2><button onclick="send('SCAN_CONTACTS',{})">Scan Contacts</button></section><section class="p"><h2>Captain</h2><button onclick="send('SET_RED_ALERT',{enabled:true})">Red Alert</button><button onclick="send('SET_RED_ALERT',{enabled:false})">Stand Down</button></section><section class="p"><h2>Authoritative State</h2><pre id="state">Not connected</pre></section></main>
        <script>document.querySelectorAll('section').forEach(s=>{const station=document.body.dataset.station;if(station!=='ALL'&&!s.querySelector('h2')?.textContent.toUpperCase().includes(station)&&!s.querySelector('h2')?.textContent.includes('Authoritative'))s.style.display='none'});let ws;const $=id=>document.getElementById(id);function connect(){ws=new WebSocket(`${location.protocol==='https:'?'wss':'ws'}://${location.host}/ws/session/${$('session').value}`);ws.onopen=()=>$('status').textContent=' CONNECTED';ws.onclose=()=>$('status').textContent=' DISCONNECTED';ws.onmessage=e=>{const m=JSON.parse(e.data);$('state').textContent=JSON.stringify(m,null,2);if(m.combatContacts?.length&&!$('target').value)$('target').value=m.combatContacts[0].id}}function send(type,data){if(!ws||ws.readyState!==1)return alert('Connect first');ws.send(JSON.stringify({protocolVersion:1,type,requestId:crypto.randomUUID(),playerId:$('player').value,...data}))}</script></body></html>
        """;

    private static final String PAGE = """
        <!doctype html>
        <html lang="en">
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width,initial-scale=1">
          <title>Beyond the Signal — Bridge Debug Console</title>
          <style>
            :root{color-scheme:dark;--bg:#071018;--panel:#0d1b26;--line:#1d3b4d;--text:#d8eef7;--muted:#7fa2b3;--good:#70e0a1;--warn:#ffd166}
            *{box-sizing:border-box}body{margin:0;background:radial-gradient(circle at top,#102b3b,var(--bg) 42%);color:var(--text);font:14px/1.45 ui-monospace,SFMono-Regular,Menlo,monospace}
            header{display:flex;gap:18px;align-items:center;padding:18px 24px;border-bottom:1px solid var(--line);background:#071018dd;position:sticky;top:0;z-index:2}
            h1{font-size:18px;margin:0;letter-spacing:.08em;text-transform:uppercase}select{background:var(--panel);color:var(--text);border:1px solid var(--line);padding:9px 12px;border-radius:6px;min-width:280px}
            main{padding:20px;max-width:1500px;margin:auto}.grid{display:grid;grid-template-columns:repeat(4,minmax(220px,1fr));gap:14px}.wide{grid-column:span 2}.panel{background:linear-gradient(180deg,#102331,#0b1822);border:1px solid var(--line);border-radius:9px;padding:16px;box-shadow:0 12px 30px #0005}.panel h2{font-size:12px;color:#8bd3ee;letter-spacing:.12em;text-transform:uppercase;margin:0 0 14px}.metric{font-size:28px;font-weight:700}.muted{color:var(--muted)}.rows{display:grid;gap:8px}.row{display:flex;justify-content:space-between;border-bottom:1px solid #18303e;padding-bottom:6px}.ok{color:var(--good)}.empty{color:var(--muted)}table{width:100%;border-collapse:collapse}td,th{text-align:left;padding:7px;border-bottom:1px solid #18303e}progress{width:100%}@media(max-width:1000px){.grid{grid-template-columns:1fr 1fr}.wide{grid-column:span 2}}@media(max-width:640px){.grid{grid-template-columns:1fr}.wide{grid-column:span 1}header{align-items:flex-start;flex-direction:column}select{width:100%;min-width:0}}
          </style>
        </head>
        <body>
          <header><h1>Bridge Debug Console</h1><select id="session"><option>Loading active sessions…</option></select><span id="status" class="muted"></span></header>
          <main><div class="grid">
            <section class="panel"><h2>Simulation Tick</h2><div class="metric" id="tick">—</div><div class="muted" id="sessionName">No active session</div></section>
            <section class="panel"><h2>Heading</h2><div class="metric"><span id="heading">—</span>°</div><div class="muted">Throttle <span id="throttle">—</span>%</div></section>
            <section class="panel"><h2>Speed</h2><div class="metric" id="speed">—</div><div class="muted">simulation units / second</div></section>
            <section class="panel"><h2>Networking</h2><div class="metric" id="sockets">—</div><div class="muted">WebSocket subscribers</div></section>
            <section class="panel wide"><h2>Position & Velocity</h2><div class="rows" id="vectors"></div></section>
            <section class="panel wide"><h2>Runtime</h2><div class="rows" id="runtime"></div></section>
            <section class="panel wide"><h2>Bridge Stations</h2><table><thead><tr><th>Station</th><th>Operator</th><th>Status</th></tr></thead><tbody id="stations"></tbody></table></section>
            <section class="panel wide"><h2>Combat Contacts</h2><table><thead><tr><th>Contact</th><th>Shields</th><th>Hull</th><th>Status</th></tr></thead><tbody id="contacts"></tbody></table><div class="muted" id="combatEvent"></div></section>
            <section class="panel wide"><h2>Ship Systems</h2><div class="rows" id="systems"></div></section>
          </div></main>
          <script>
            const $=id=>document.getElementById(id), select=$('session'); let timer;
            const n=(v,d=2)=>Number(v??0).toFixed(d);
            async function loadSessions(){const r=await fetch('/api/debug/bridge/sessions');const j=await r.json();select.innerHTML='';j.sessions.forEach(s=>select.add(new Option(`${s.sessionName} — ${s.shipName}`,s.id)));if(j.sessions.length){load();timer=setInterval(load,500)}else{$('status').textContent='No active simulations'}}
            async function load(){if(!select.value)return;try{const r=await fetch(`/api/debug/bridge/sessions/${select.value}`,{cache:'no-store'});if(!r.ok)throw new Error(await r.text());const d=await r.json();$('status').textContent=`${d.session.status} • ${new Date().toLocaleTimeString()}`;$('tick').textContent=d.ship.tick.toLocaleString();$('sessionName').textContent=`${d.session.sessionName} / ${d.session.shipName}`;$('heading').textContent=n(d.ship.headingDegrees,1);$('throttle').textContent=d.ship.throttle;$('speed').textContent=n(d.ship.speed,2);$('sockets').textContent=d.runtime.websocketSubscribers;$('vectors').innerHTML=['position','velocity'].map(k=>`<div class="row"><span>${k.toUpperCase()}</span><b>X ${n(d.ship[k].x)} &nbsp; Y ${n(d.ship[k].y)} &nbsp; Z ${n(d.ship[k].z)}</b></div>`).join('');$('runtime').innerHTML=`<div class="row"><span>Configured rate</span><b>${n(d.runtime.configuredTickRateHz,1)} Hz</b></div><div class="row"><span>Last tick</span><b>${n(d.runtime.lastTickDurationMillis,3)} ms</b></div><div class="row"><span>Average tick</span><b>${n(d.runtime.averageTickDurationMillis,3)} ms</b></div><div class="row"><span>Command queue</span><b>${d.runtime.commandQueueDepth}</b></div>`;$('stations').innerHTML=d.stations.map(s=>`<tr><td>${s.station}</td><td>${s.displayName??'Unassigned'}</td><td class="${s.connected?'ok':'empty'}">${s.displayName?(s.connected?'CONNECTED':'OFFLINE'):'EMPTY'}</td></tr>`).join('');$('contacts').innerHTML=d.ship.combatContacts.map(c=>`<tr><td>${c.displayName}${d.ship.selectedTargetId===c.id?' • SELECTED':''}</td><td>${c.shieldStrength}%</td><td>${c.hullIntegrity}%</td><td class="${c.destroyed?'empty':'ok'}">${c.destroyed?'DESTROYED':'ACTIVE'}</td></tr>`).join('');const e=d.ship.lastCombatEvent;$('combatEvent').textContent=e?`Last impact: ${e.weapon} • shield -${e.shieldDamage} • hull -${e.hullDamage}${e.targetDestroyed?' • TARGET DESTROYED':''}`:'No weapon impacts recorded';$('systems').innerHTML=Object.entries(d.ship.subsystems).map(([name,s])=>`<div><div class="row"><span>${name}</span><b>Health ${s.health}% • Power ${s.powerAllocation}%</b></div><progress max="100" value="${s.health}"></progress></div>`).join('')}catch(e){$('status').textContent='Diagnostics unavailable'}}
            select.addEventListener('change',load);loadSessions();
          </script>
        </body></html>
        """;
}
