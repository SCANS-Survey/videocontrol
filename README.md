# Video Control
Control of video cameras and recording hardware.

This is a development of the ATOMOS / AMP recorder control built for SCANS III which could start and stop an ATOMOS Shogun recorder. 

Rewritten as a PAMGuard plugin to control any number of video cameras, issuing start / stop commands.

Currently targeting:
1. Advanced Media Control (AMP) - works with some Ethernet connected devices, e.g. ATOMOS
2. LANC - works over a serial interface
3. Panasonic Lumix - bespoke HTTP commands
4. Canon Camera Control API (CCAPI) - bespoke HTTP commands to control Canon cameras

None of these are currently functional

Interfaces to PAMGuard as a plugin module that will show each configured camera as a Button Action with the logger forms modules of PAMGuard. 
