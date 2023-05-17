import { Smallville, getLeafLocation } from '../lib/smallville.js'
import fetch from 'cross-fetch';
import { jest } from '@jest/globals'

describe('getLeafLocation', () => {
    it('should return the last element of a colon-separated string', () => {
        expect(getLeafLocation('Root: child: leaf')).toBe('leaf');
        expect(getLeafLocation('One: Two: Three: Four')).toBe('Four');
        expect(getLeafLocation('leaf')).toBe('leaf');
    });

    it('should handle leading and trailing spaces in the input string', () => {
        expect(getLeafLocation('  Root: child: leaf  ')).toBe('leaf');
        expect(getLeafLocation('   One: Two: Three: Four  ')).toBe('Four');
        expect(getLeafLocation('  leaf  ')).toBe('leaf');
    });
});