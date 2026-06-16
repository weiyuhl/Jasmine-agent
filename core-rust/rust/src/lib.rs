uniffi::include_scaffolding!("jasmine_core");

const MIN_AGENT_NAME_LENGTH: i32 = 2;
const MAX_AGENT_NAME_LENGTH: i32 = 100;

#[derive(Debug, Clone)]
pub enum AgentNameValidationKind {
    Valid,
    EmptyInput,
    TooShort,
    TooLong,
    InvalidCharacters,
}

#[derive(Debug, Clone)]
pub struct AgentNameValidation {
    pub kind: AgentNameValidationKind,
    pub actual: i32,
    pub limit: i32,
    pub invalid_characters: String,
}

pub fn normalize_agent_name(name: String) -> String {
    name.trim().to_string()
}

pub fn validate_agent_name(name: String) -> AgentNameValidation {
    let normalized = normalize_agent_name(name);
    let actual = normalized.chars().count() as i32;
    let invalid_characters = invalid_characters(&normalized);

    if normalized.is_empty() {
        return validation(AgentNameValidationKind::EmptyInput, actual, 0, String::new());
    }

    if actual < MIN_AGENT_NAME_LENGTH {
        return validation(
            AgentNameValidationKind::TooShort,
            actual,
            MIN_AGENT_NAME_LENGTH,
            String::new(),
        );
    }

    if actual > MAX_AGENT_NAME_LENGTH {
        return validation(
            AgentNameValidationKind::TooLong,
            actual,
            MAX_AGENT_NAME_LENGTH,
            String::new(),
        );
    }

    if !invalid_characters.is_empty() {
        return validation(
            AgentNameValidationKind::InvalidCharacters,
            actual,
            0,
            invalid_characters,
        );
    }

    validation(AgentNameValidationKind::Valid, actual, 0, String::new())
}

fn validation(
    kind: AgentNameValidationKind,
    actual: i32,
    limit: i32,
    invalid_characters: String,
) -> AgentNameValidation {
    AgentNameValidation {
        kind,
        actual,
        limit,
        invalid_characters,
    }
}

fn invalid_characters(value: &str) -> String {
    let mut chars = value
        .chars()
        .filter(|character| {
            !character.is_alphanumeric()
                && !character.is_whitespace()
                && !matches!(character, '-' | '_' | '.')
        })
        .collect::<Vec<_>>();
    chars.sort_unstable();
    chars.dedup();
    chars.into_iter().collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn validates_agent_name() {
        assert!(matches!(
            validate_agent_name("Valid-Agent_123.0".to_string()).kind,
            AgentNameValidationKind::Valid
        ));
    }

    #[test]
    fn reports_invalid_characters() {
        let result = validate_agent_name("Agent@#$%".to_string());

        assert!(matches!(
            result.kind,
            AgentNameValidationKind::InvalidCharacters
        ));
        assert_eq!("#$%@", result.invalid_characters);
    }
}
