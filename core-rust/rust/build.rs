fn main() {
    uniffi::generate_scaffolding("src/jasmine_core.udl").expect("generate UniFFI scaffolding");
}
