package it.gov.pagopa.pu.organization.exception.transcoder;

public interface ExceptionMessageTranscoder<T extends Exception> {
  ExceptionMessageTranscoded transcode(T exception);
}
