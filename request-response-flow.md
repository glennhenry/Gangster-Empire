Legend:

- `<sf>`   = SmartFox XML (always starts with `<`)
- `<xt>`   = SmartFox Extension (plain string, `%`-separated)
- `<xtpb>` = SmartFox Extension (Protobuf payload)
- `<json>` = SmartFox JSON (always starts with `{`)

---

## Order of Request/Response Commands

1. `<sf> verChk`  
   → `<sf> apiOk`

2. `<sf> login`  
   → `<xt> rlu`

3. `<sf> autoJoin`  
   → `<sf> joinOk`

4. `<sf> roundTrip`  
   → `<sf> roundTripResponse`

5. `<xt> pin`  
   → *ignorable*

6. `<xt> vck`  
   → `<xt> vck`

---

### Registration vs Login Branch

- **If Register**  
  7a. `<xt> createavatar` → `<xt> createavatar`  
  7b. `<xt> lre`          → `<xt> lre`

- **If Login**  
  7c. `<xt> lgn`          → `<xt> lgn`

---

### 8. `<xt> apd` → Response Data Chain

When `apd` is requested, the server responds with a sequence of extension calls:

- `<xt>   oga`
- `<xt>   sgc`
- `<xt>   oio`
- `<xtpb> playerprofile`
- `<xtpb> newachievements`
- `<xt>   paymentinfo`
- `<xt>   oud`
- `<xtpb> playercurrency`
- `<xtpb> viewarmament`
- `<xtpb> getarmamentpresetstatus`
- `<xtpb> viewgear`
- `<xtpb> viewfood`
- `<xtpb> viewinventory`
- `<xtpb> viewitems (black market)`
- `<xtpb> viewitems (consumable)`
- `<xtpb> viewitems (kiosk)`
- `<xt>   auc`
- `<xtpb> getplayerbooster`
- `<xtpb> showmissionbooster`
- `<xtpb> viewmissions`
- `<xtpb> viewwork`
- `<xt>   png`
- `<xt>   sae`
- `<xt>   lfe`
- `<xt>   gch`
- `<xt>   gfl`
- `<xt>   getactivequests`
- `<xt>   sgs`
- `<xt>   sga`
- `<xt>   apd` (final confirmation after everything)

---
