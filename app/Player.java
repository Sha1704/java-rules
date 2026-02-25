package app;

// SER11-J: If Externalizable is ever used, add a guard in readExternal()
// to prevent multiple initialization (e.g., boolean initialized flag).
// For Serializable, readObject is called only once automatically, so no guard needed.
// ^^ Dariya, ask me if that makes no sense its a note for me later when this is in -Carlos 2/25
public class Player {
    
}